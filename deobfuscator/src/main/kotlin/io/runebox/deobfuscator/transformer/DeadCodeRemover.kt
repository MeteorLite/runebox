package io.runebox.deobfuscator.transformer

import io.runebox.asm.tree.ClassPool
import io.runebox.asm.utils.InstructionModifier
import io.runebox.asm.utils.Utils
import io.runebox.deobfuscator.Transformer
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.BasicInterpreter
import org.tinylog.kotlin.Logger

class DeadCodeRemover : Transformer {

    private var count = 0

    override fun run(pool: ClassPool) {
        pool.classes.forEach { cls ->
            cls.methods.forEach methodLoop@ { method ->
                if(method.instructions.first == null) return@methodLoop

                val insns = method.instructions.toArray()
                val frames = Analyzer(BasicInterpreter()).analyze(cls.name, method)
                val modifier = InstructionModifier()

                for(i in insns.indices) {
                    if(!Utils.isInstruction(insns[i])) continue
                    if(frames[i] != null) continue
                    modifier.remove(insns[i])
                    count++
                }
                modifier.apply(method)

                if(method.tryCatchBlocks != null) {
                    method.tryCatchBlocks.removeIf {
                        Utils.getNext(it.start) == Utils.getNext(it.end)
                    }
                }
            }
        }

        Logger.info("Removed $count dead instruction opcodes.")
    }
}