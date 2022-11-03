package io.runebox.deobfuscator.transformer

import io.runebox.asm.LabelMap
import io.runebox.asm.graph.Converter
import io.runebox.asm.tree.ClassPool
import io.runebox.asm.tree.owner
import io.runebox.deobfuscator.Transformer
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.AbstractInsnNode.*
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.BasicInterpreter
import org.objectweb.asm.tree.analysis.BasicValue
import org.tinylog.kotlin.Logger
import java.util.*

class ControlFlowFixer : Transformer {

    private var count = 0

    override fun run(pool: ClassPool) {
        pool.classes.forEach { cls ->
            cls.methods.forEach methodLoop@ { method ->
                if(method.tryCatchBlocks.isNotEmpty()) return@methodLoop
                val cfg = ControlFlowGraph(method)
                val newInsns = InsnList()
                val labelMap = LabelMap()

                if(cfg.blocks.isNotEmpty()) {
                    val queue = Collections.asLifoQueue(ArrayDeque<Block>())
                    val placed = hashSetOf<Block>()
                    queue.add(cfg.blocks.first())
                    while(queue.isNotEmpty()) {
                        val block = queue.remove()
                        if(block in placed) continue
                        placed.add(block)
                        block.branches.forEach { queue.add(it.head) }
                        if(block.next != null) {
                            queue.add(block.next)
                        }
                        for(i in block.startInsn until block.endInsn) {
                            newInsns.add(method.instructions[i].clone(labelMap))
                        }
                    }
                }
                method.instructions = newInsns
                count += cfg.blocks.size
            }
        }


        Logger.info("Reordered $count method control-flow blocks.")
    }

    private class ControlFlowGraph(private val method: MethodNode) {

        val blocks = mutableListOf<Block>()
        val exceptionBlocks = mutableListOf<Block>()

        private val analyzer = object : Analyzer<BasicValue>(BasicInterpreter()) {

            override fun init(owner: String, method: MethodNode) {
                val insns = method.instructions.toArray()
                var block = Block()
                blocks.add(block)
                for(i in insns.indices) {
                    val insn = insns[i]
                    block.endInsn++
                    block.instructions.add(insn)
                    if(insn.next == null) continue
                    if(insn.next.type == LABEL || insn.type in arrayOf(JUMP_INSN, TABLESWITCH_INSN, LOOKUPSWITCH_INSN)) {
                        block = Block()
                        block.startInsn = i + 1
                        block.endInsn = i + 1
                        block.instructions.add(insn.next)
                        blocks.add(block)
                    }
                }
            }

            override fun newControlFlowEdge(insnIndex: Int, successorIndex: Int) {
                val block1 = blocks.first { insnIndex in it.startInsn until it.endInsn }
                val block2 = blocks.first { successorIndex in it.startInsn until it.endInsn }
                if(block1 != block2) {
                    if(insnIndex + 1 == successorIndex) {
                        block1.next = block2
                        block2.prev = block1
                    } else {
                        block1.branches.add(block2)
                    }
                }
            }

            override fun newControlFlowExceptionEdge(insnIndex: Int, successorIndex: Int): Boolean {
                newControlFlowEdge(insnIndex, successorIndex)
                return super.newControlFlowExceptionEdge(insnIndex, successorIndex)
            }
        }

        init {
            analyzer.analyze(method.owner.name, method)
        }
    }

    private class Block {

        val instructions = mutableListOf<AbstractInsnNode>()

        var prev: Block? = null

        var next: Block? = null

        var startInsn: Int = 0
        var endInsn: Int = 0

        val branches = mutableListOf<Block>()

        val head: Block get() {
            var cur: Block? = this
            var ret = cur
            while(cur != null) {
                ret = cur
                cur = cur.prev
            }
            return ret!!
        }
    }
}