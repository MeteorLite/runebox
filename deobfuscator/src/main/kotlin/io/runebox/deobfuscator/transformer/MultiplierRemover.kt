package io.runebox.deobfuscator.transformer

import io.runebox.asm.tree.ClassPool
import io.runebox.deobfuscator.Transformer
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.Interpreter
import org.objectweb.asm.tree.analysis.SourceInterpreter
import org.objectweb.asm.tree.analysis.SourceValue
import org.objectweb.asm.tree.analysis.Value
import org.objectweb.asm.util.Printer
import java.math.BigInteger

class MultiplierRemover : Transformer {

    override fun run(pool: ClassPool) {
        val finder = MultiplierFinder(pool)
        finder.run()

        println()
    }

}

private class MultiplierFinder(private val pool: ClassPool) {

    private val interpreter = MulInterpreter(this)
    private val analyzer = Analyzer(interpreter)

    fun run() {
        pool.getClass("class160")!!.let { cls ->
            cls.methods.forEach { method ->
                try { analyzer.analyze(cls.name, method) } catch(e: Exception) {
                    /* Do Nothing */
                }
            }
        }

        println()
    }

    open class Expr(val src: SourceValue) : Value {
        override fun getSize(): Int = src.size
        override fun equals(other: Any?): Boolean = other is Expr && other.src == src
        override fun hashCode(): Int = src.hashCode()

        val children = mutableListOf<Expr>()

        override fun toString(): String {
            return if(src.insns.single().opcode == NOP) "NOP" else Printer.OPCODES[src.insns.single().opcode]
        }

        class Two(src: SourceValue, val left: Expr, val right: Expr) : Expr(src) {
            init {
                children.addAll(left.children)
                children.addAll(right.children)
            }
            override fun toString(): String = "${super.toString()}(left: $left, right: $right)"
        }

        val SourceValue.insn get() = insns.single()
        val insn get() = src.insn
    }

    class MulInterpreter(private val parent: MultiplierFinder) : Interpreter<Expr>(ASM9) {

        private val interp = SourceInterpreter()

        private val mults = mutableListOf<Expr.Two>()

        override fun newValue(type: Type?) = interp.newValue(type)?.let { Expr(it) }

        override fun newOperation(insn: AbstractInsnNode) = Expr(interp.newOperation(insn))

        override fun merge(value1: Expr, value2: Expr): Expr {
            if(value1 == value2) {
                return value1
            } else {
                if(value1 is Expr.Two && value2 is Expr.Two && value1.insn == value2.insn) {
                    if(value1.left == value2.left && value1.left is Expr.Two) {
                        return Expr.Two(value1.src, value1.left, merge(value1.right, value2.right))
                    } else if(value1.right == value2.right && value1.right is Expr.Two) {
                        return Expr.Two(value1.src, merge(value1.left, value2.left), value1.right)
                    }
                }
            }
            return Expr(interp.merge(value1.src, value2.src))
        }

        override fun returnOperation(insn: AbstractInsnNode, value: Expr, expected: Expr) {}

        override fun naryOperation(insn: AbstractInsnNode, values: MutableList<out Expr>) = Expr(interp.naryOperation(insn, values.map { it.src }))

        override fun ternaryOperation(insn: AbstractInsnNode, value1: Expr, value2: Expr, value3: Expr) = Expr(interp.ternaryOperation(insn, value1.src, value2.src, value3.src))

        override fun binaryOperation(insn: AbstractInsnNode, value1: Expr, value2: Expr) = Expr.Two(interp.binaryOperation(insn, value1.src, value2.src), value1, value2).also {
            when(insn.opcode) {
                IMUL, LMUL -> {
                    it.children.addAll(arrayOf(value1, value2))
                    mults.add(it)
                }
            }
        }

        override fun unaryOperation(insn: AbstractInsnNode, value: Expr) = Expr(interp.unaryOperation(insn, value.src))

        override fun copyOperation(insn: AbstractInsnNode, value: Expr) = Expr(interp.copyOperation(insn, value.src))
    }
}

class MultiplierPair(
    val product: BigInteger,
    val quotient: BigInteger,
    val gcd: BigInteger,
    val bits: Int
) {
    val trueValue = gcd.multiply(product).let { quotient.multiply(it) }
}