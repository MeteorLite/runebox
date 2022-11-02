package io.runebox.deobfuscator.transformer

import io.runebox.asm.tree.ClassPool
import io.runebox.deobfuscator.Transformer
import io.runebox.deobfuscator.util.iterate
import org.objectweb.asm.Type
import org.tinylog.kotlin.Logger

class RuntimeExceptionRemover : Transformer {

    private var count = 0

    override fun run(pool: ClassPool) {
        pool.classes.forEach { cls ->
            cls.methods.forEach methodLoop@ { method ->
                if(method.instructions == null || method.instructions.size() == 0) {
                    return@methodLoop
                }

                if(cls.name == "client" && method.name == "init") {
                    return@methodLoop
                }

                method.tryCatchBlocks.iterate { tcb ->
                    if(tcb.type == Type.getInternalName(java.lang.RuntimeException::class.java)) {
                        this.remove()
                        count++
                    }
                }
            }
        }

        Logger.info("Removed $count 'RuntimeException' try-catch blocks.")
    }
}