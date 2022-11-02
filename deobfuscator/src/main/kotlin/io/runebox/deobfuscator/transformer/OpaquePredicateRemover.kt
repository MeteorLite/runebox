package io.runebox.deobfuscator.transformer

import io.runebox.asm.matcher.CapturingStep
import io.runebox.asm.matcher.InstructionPattern
import io.runebox.asm.matcher.InvocationStep
import io.runebox.asm.matcher.OpcodeStep
import io.runebox.asm.tree.ClassPool
import io.runebox.deobfuscator.Transformer
import org.objectweb.asm.Opcodes.*
import org.tinylog.kotlin.Logger

class OpaquePredicateRemover : Transformer {

    private var checkCount = 0
    private var argCount = 0

    override fun run(pool: ClassPool) {
        removeOpaqueChecks(pool)
        Logger.info("Removed $checkCount opaque check and $argCount opaque args.")
    }

    private fun removeOpaqueChecks(pool: ClassPool) {

    }

    companion object {

        private val OPAQUE_EXCEPTION_PATTERN = InstructionPattern(
            CapturingStep(OpcodeStep(ILOAD), "var"),
            OpcodeStep(LDC),
            CapturingStep(OpcodeStep(IF_ICMPEQ, IF_ICMPNE, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ICMPLT), "jump"),
            CapturingStep(OpcodeStep(NEW), "exception"),
            OpcodeStep(DUP),
            OpcodeStep(INVOKESPECIAL),
            OpcodeStep(ATHROW)
        )
    }
}