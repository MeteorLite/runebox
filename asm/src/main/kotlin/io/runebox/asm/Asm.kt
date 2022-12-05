/*
 * Copyright (C) 2022 RuneBox <Kyle Escobar>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.runebox.asm

import io.runebox.asm.tree.ClassPool
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.jar.JarFile
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FrameNode
import org.objectweb.asm.tree.IincInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.BasicInterpreter
import org.objectweb.asm.tree.analysis.BasicValue
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.LookupSwitchInsnNode
import org.objectweb.asm.tree.TableSwitchInsnNode
import org.objectweb.asm.tree.TryCatchBlockNode
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceMethodVisitor
import java.io.PrintWriter
import java.io.StringWriter
import org.objectweb.asm.tree.InsnList
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

fun readClass(data: ByteArray): ClassNode {
    val node = ClassNode()
    val reader = ClassReader(data)
    reader.accept(node, ClassReader.SKIP_FRAMES)
    return node
}

fun writeClass(node: ClassNode): ByteArray {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    node.accept(writer)
    return writer.toByteArray()
}

fun ClassPool.addJar(file: File) {
    JarFile(file).use { jar ->
        jar.entries().asSequence().forEach { entry ->
            if(!entry.name.endsWith(".class")) return@forEach
            val node = readClass(jar.getInputStream(entry).readAllBytes())
            this.addClass(node)
        }
    }
}

fun ClassPool.writeJar(file: File) {
    if(file.exists()) file.deleteRecursively()
    JarOutputStream(FileOutputStream(file)).use { jos ->
        allClasses.forEach { cls ->
            jos.putNextEntry(JarEntry(cls.name+".class"))
            jos.write(writeClass(cls))
            jos.closeEntry()
        }
    }
}

private val PURE_OPCODES = setOf(
    -1,
    Opcodes.NOP,
    Opcodes.ACONST_NULL,
    Opcodes.ICONST_M1,
    Opcodes.ICONST_0,
    Opcodes.ICONST_1,
    Opcodes.ICONST_2,
    Opcodes.ICONST_3,
    Opcodes.ICONST_4,
    Opcodes.ICONST_5,
    Opcodes.LCONST_0,
    Opcodes.LCONST_1,
    Opcodes.FCONST_0,
    Opcodes.FCONST_1,
    Opcodes.FCONST_2,
    Opcodes.DCONST_0,
    Opcodes.DCONST_1,
    Opcodes.BIPUSH,
    Opcodes.SIPUSH,
    Opcodes.LDC,
    Opcodes.ILOAD,
    Opcodes.LLOAD,
    Opcodes.FLOAD,
    Opcodes.DLOAD,
    Opcodes.ALOAD,
    Opcodes.POP,
    Opcodes.POP2,
    Opcodes.DUP,
    Opcodes.DUP_X1,
    Opcodes.DUP_X2,
    Opcodes.DUP2,
    Opcodes.DUP2_X1,
    Opcodes.DUP2_X2,
    Opcodes.SWAP,
    Opcodes.IADD,
    Opcodes.LADD,
    Opcodes.FADD,
    Opcodes.DADD,
    Opcodes.ISUB,
    Opcodes.LSUB,
    Opcodes.FSUB,
    Opcodes.DSUB,
    Opcodes.IMUL,
    Opcodes.LMUL,
    Opcodes.FMUL,
    Opcodes.DMUL,
    /*
     * XXX(gpe): strictly speaking the *DEV and *REM instructions have side
     * effects (unless we can prove that the second argument is non-zero).
     * However, treating them as having side effects reduces the number of
     * dummy variables we can remove, so we pretend they don't have any side
     * effects.
     *
     * This doesn't seem to cause any problems with the client, as it doesn't
     * deliberately try to trigger divide-by-zero exceptions.
     */
    Opcodes.IDIV,
    Opcodes.LDIV,
    Opcodes.FDIV,
    Opcodes.DDIV,
    Opcodes.IREM,
    Opcodes.LREM,
    Opcodes.FREM,
    Opcodes.DREM,
    Opcodes.INEG,
    Opcodes.LNEG,
    Opcodes.FNEG,
    Opcodes.DNEG,
    Opcodes.ISHL,
    Opcodes.LSHL,
    Opcodes.ISHR,
    Opcodes.LSHR,
    Opcodes.IUSHR,
    Opcodes.LUSHR,
    Opcodes.IAND,
    Opcodes.LAND,
    Opcodes.IOR,
    Opcodes.LOR,
    Opcodes.IXOR,
    Opcodes.LXOR,
    Opcodes.I2L,
    Opcodes.I2F,
    Opcodes.I2D,
    Opcodes.L2I,
    Opcodes.L2F,
    Opcodes.L2D,
    Opcodes.F2I,
    Opcodes.F2L,
    Opcodes.F2D,
    Opcodes.D2I,
    Opcodes.D2L,
    Opcodes.D2F,
    Opcodes.I2B,
    Opcodes.I2C,
    Opcodes.I2S,
    Opcodes.LCMP,
    Opcodes.FCMPL,
    Opcodes.FCMPG,
    Opcodes.DCMPL,
    Opcodes.DCMPG,
    Opcodes.GETSTATIC,
    Opcodes.NEW,
    Opcodes.INSTANCEOF
)
private val IMPURE_OPCODES = setOf(
    Opcodes.IALOAD,
    Opcodes.LALOAD,
    Opcodes.FALOAD,
    Opcodes.DALOAD,
    Opcodes.AALOAD,
    Opcodes.BALOAD,
    Opcodes.CALOAD,
    Opcodes.SALOAD,
    Opcodes.ISTORE,
    Opcodes.LSTORE,
    Opcodes.FSTORE,
    Opcodes.DSTORE,
    Opcodes.ASTORE,
    Opcodes.IASTORE,
    Opcodes.LASTORE,
    Opcodes.FASTORE,
    Opcodes.DASTORE,
    Opcodes.AASTORE,
    Opcodes.BASTORE,
    Opcodes.CASTORE,
    Opcodes.SASTORE,
    Opcodes.IINC,
    Opcodes.IFEQ,
    Opcodes.IFNE,
    Opcodes.IFLT,
    Opcodes.IFGE,
    Opcodes.IFGT,
    Opcodes.IFLE,
    Opcodes.IF_ICMPEQ,
    Opcodes.IF_ICMPNE,
    Opcodes.IF_ICMPLT,
    Opcodes.IF_ICMPGE,
    Opcodes.IF_ICMPGT,
    Opcodes.IF_ICMPLE,
    Opcodes.IF_ACMPEQ,
    Opcodes.IF_ACMPNE,
    Opcodes.GOTO,
    Opcodes.JSR,
    Opcodes.RET,
    Opcodes.TABLESWITCH,
    Opcodes.LOOKUPSWITCH,
    Opcodes.IRETURN,
    Opcodes.LRETURN,
    Opcodes.FRETURN,
    Opcodes.DRETURN,
    Opcodes.ARETURN,
    Opcodes.RETURN,
    Opcodes.PUTSTATIC,
    Opcodes.GETFIELD,
    Opcodes.PUTFIELD,
    Opcodes.INVOKEVIRTUAL,
    Opcodes.INVOKESPECIAL,
    Opcodes.INVOKESTATIC,
    Opcodes.INVOKEINTERFACE,
    Opcodes.INVOKEDYNAMIC,
    Opcodes.NEWARRAY,
    Opcodes.ANEWARRAY,
    Opcodes.ARRAYLENGTH,
    Opcodes.ATHROW,
    Opcodes.CHECKCAST,
    Opcodes.MONITORENTER,
    Opcodes.MONITOREXIT,
    Opcodes.MULTIANEWARRAY,
    Opcodes.IFNULL,
    Opcodes.IFNONNULL
)
private val THROW_RETURN_OPCODES = listOf(
    Opcodes.IRETURN,
    Opcodes.LRETURN,
    Opcodes.FRETURN,
    Opcodes.DRETURN,
    Opcodes.ARETURN,
    Opcodes.RETURN,
    Opcodes.RET,
    Opcodes.ATHROW
)
val AbstractInsnNode.nextReal: AbstractInsnNode?
    get() {
        var insn = next
        while (insn != null && insn.opcode == -1) {
            insn = insn.next
        }
        return insn
    }
val AbstractInsnNode.previousReal: AbstractInsnNode?
    get() {
        var insn = previous
        while (insn != null && insn.opcode == -1) {
            insn = insn.previous
        }
        return insn
    }
val AbstractInsnNode.nextVirtual: AbstractInsnNode?
    get() {
        var insn = next
        while (insn != null && insn.opcode != -1) {
            insn = insn.next
        }
        return insn
    }
val AbstractInsnNode.previousVirtual: AbstractInsnNode?
    get() {
        var insn = previous
        while (insn != null && insn.opcode != -1) {
            insn = insn.previous
        }
        return insn
    }
val AbstractInsnNode.intConstant: Int?
    get() = when (this) {
        is IntInsnNode -> {
            if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
                operand
            } else {
                null
            }
        }
        is LdcInsnNode -> {
            val cst = cst
            if (cst is Int) {
                cst
            } else {
                null
            }
        }
        else -> when (opcode) {
            Opcodes.ICONST_M1 -> -1
            Opcodes.ICONST_0 -> 0
            Opcodes.ICONST_1 -> 1
            Opcodes.ICONST_2 -> 2
            Opcodes.ICONST_3 -> 3
            Opcodes.ICONST_4 -> 4
            Opcodes.ICONST_5 -> 5
            else -> null
        }
    }
public val AbstractInsnNode.isSequential: Boolean
    get() = when (this) {
        is LabelNode -> false
        is JumpInsnNode -> false
        is TableSwitchInsnNode -> false
        is LookupSwitchInsnNode -> false
        else -> opcode !in THROW_RETURN_OPCODES
    }
public val AbstractInsnNode.isPure: Boolean
    get() = when (opcode) {
        in PURE_OPCODES -> true
        in IMPURE_OPCODES -> false
        else -> throw IllegalArgumentException()
    }

fun createIntConstant(value: Int): AbstractInsnNode = when (value) {
    -1 -> InsnNode(Opcodes.ICONST_M1)
    0 -> InsnNode(Opcodes.ICONST_0)
    1 -> InsnNode(Opcodes.ICONST_1)
    2 -> InsnNode(Opcodes.ICONST_2)
    3 -> InsnNode(Opcodes.ICONST_3)
    4 -> InsnNode(Opcodes.ICONST_4)
    5 -> InsnNode(Opcodes.ICONST_5)
    in Byte.MIN_VALUE..Byte.MAX_VALUE -> IntInsnNode(Opcodes.BIPUSH, value)
    in Short.MIN_VALUE..Short.MAX_VALUE -> IntInsnNode(Opcodes.SIPUSH, value)
    else -> LdcInsnNode(value)
}
fun AbstractInsnNode.toPrettyString(): String {
    val printer = Textifier()
    val visitor = TraceMethodVisitor(printer)
    accept(visitor)
    StringWriter().use { stringWriter ->
        PrintWriter(stringWriter).use { printWriter ->
            printer.print(printWriter)
            return stringWriter.toString().trim()
        }
    }
}
fun TryCatchBlockNode.isBodyEmpty(): Boolean {
    var current = start.next
    while (true) {
        when {
            current == null -> return false
            current === end -> return true
            current.opcode != -1 -> return false
            else -> current = current.next
        }
    }
}

private fun localIndex(access: Int, argTypes: Array<Type>, argIndex: Int): Int {
    var localIndex = 0
    if (access and Opcodes.ACC_STATIC == 0) {
        localIndex++
    }
    for (i in 0 until argIndex) {
        localIndex += argTypes[i].size
    }
    return localIndex
}
private fun remap(i: Int, argType: Type, localIndex: Int): Int {
    return if (i >= localIndex) {
        i - argType.size
    } else {
        i
    }
}
private fun remapAll(indexes: List<Int>, argType: Type, localIndex: Int): MutableList<Int> {
    return indexes.mapTo(mutableListOf()) { remap(it, argType, localIndex) }
}
fun MethodNode.removeArgument(argIndex: Int) {
    // remove argument from the descriptor
    val type = Type.getType(desc)
    val argType = type.argumentTypes[argIndex]
    val argTypes = type.argumentTypes.filterIndexed { index, _ -> index != argIndex }.toTypedArray()
    desc = Type.getMethodDescriptor(type.returnType, *argTypes)
    // the client doesn't use signatures so don't bother with them
    if (signature != null) {
        throw UnsupportedOperationException("Signatures unsupported")
    }
    parameters?.removeAt(argIndex)
    // remove annotations
    if (visibleAnnotableParameterCount != 0) {
        throw UnsupportedOperationException("Non-zero visibleAnnotableParameterCount unsupported")
    }
    if (visibleParameterAnnotations != null) {
        visibleParameterAnnotations =
            visibleParameterAnnotations.filterIndexed { index, _ -> index != argIndex }.toTypedArray()
    }
    if (invisibleAnnotableParameterCount != 0) {
        throw UnsupportedOperationException("Non-zero invisibleAnnotableParameterCount unsupported")
    }
    if (invisibleParameterAnnotations != null) {
        invisibleParameterAnnotations =
            invisibleParameterAnnotations.filterIndexed { index, _ -> index != argIndex }.toTypedArray()
    }
    // remap locals
    val localIndex = localIndex(access, argTypes, argIndex)
    maxLocals -= argType.size
    if (localVariables != null) {
        localVariables.removeIf { it.index == localIndex }
        for (v in localVariables) {
            v.index = remap(v.index, argType, localIndex)
        }
    }
    if (visibleLocalVariableAnnotations != null) {
        visibleLocalVariableAnnotations.removeIf { localIndex in it.index }
        for (annotation in visibleLocalVariableAnnotations) {
            annotation.index = remapAll(annotation.index, argType, localIndex)
        }
    }
    if (invisibleLocalVariableAnnotations != null) {
        invisibleLocalVariableAnnotations.removeIf { localIndex in it.index }
        for (annotation in invisibleLocalVariableAnnotations) {
            annotation.index = remapAll(annotation.index, argType, localIndex)
        }
    }
    for (insn in instructions) {
        when (insn) {
            is VarInsnNode -> insn.`var` = remap(insn.`var`, argType, localIndex)
            is IincInsnNode -> insn.`var` = remap(insn.`var`, argType, localIndex)
            is FrameNode -> throw UnsupportedOperationException("SKIP_FRAMES and COMPUTE_FRAMES must be used")
        }
    }
}
fun MethodNode.removeDeadCode(owner: String) {
    var changed: Boolean
    do {
        changed = false
        val analyzer = Analyzer<BasicValue>(BasicInterpreter())
        val frames = analyzer.analyze(owner, this)
        val deadLabels = mutableSetOf<LabelNode>()
        val it = instructions.iterator()
        var i = 0
        for (insn in it) {
            if (frames[i++] != null) {
                continue
            }
            if (insn is LabelNode) {
                deadLabels.add(insn)
            } else {
                it.remove()
                changed = true
            }
        }
        changed = changed or tryCatchBlocks.removeIf {
            it.start in deadLabels && it.end in deadLabels || it.isBodyEmpty()
        }
    } while (changed)
}
fun MethodNode.hasCode(): Boolean {
    return access and (Opcodes.ACC_NATIVE or Opcodes.ACC_ABSTRACT) == 0
}

fun MethodNode.copy(): MethodNode {
    val copy = MethodNode(
        access,
        name,
        desc,
        signature,
        exceptions?.toTypedArray()
    )
    accept(copy)
    return copy
}

private val ANY_INSN = { _: AbstractInsnNode -> true }
public fun getExpression(
    last: AbstractInsnNode,
    filter: (AbstractInsnNode) -> Boolean = ANY_INSN
): List<AbstractInsnNode>? {
    val expr = mutableListOf<AbstractInsnNode>()
    var height = 0
    var insn: AbstractInsnNode? = last
    do {
        val (pops, pushes) = insn!!.stackMetadata
        if (insn !== last) {
            expr.add(insn)
            height -= pushes
        }
        height += pops
        if (height == 0) {
            return expr.asReversed()
        }
        insn = insn.previous
    } while (insn != null && insn.isSequential && filter(insn))
    return null
}
public fun InsnList.replaceExpression(
    last: AbstractInsnNode,
    replacement: AbstractInsnNode,
    filter: (AbstractInsnNode) -> Boolean = ANY_INSN
): Boolean {
    val expr = getExpression(last, filter) ?: return false
    expr.forEach(this::remove)
    this[last] = replacement
    return true
}
public fun InsnList.deleteExpression(
    last: AbstractInsnNode,
    filter: (AbstractInsnNode) -> Boolean = ANY_INSN
): Boolean {
    val expr = getExpression(last, filter) ?: return false
    expr.forEach(this::remove)
    remove(last)
    return true
}
public fun InsnList.clone(labels: Map<LabelNode, LabelNode>): InsnList {
    val copy = InsnList()
    for (insn in this) {
        copy.add(insn.clone(labels))
    }
    return copy
}