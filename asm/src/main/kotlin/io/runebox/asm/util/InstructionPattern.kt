package io.runebox.asm.util

import io.runebox.asm.matcher.*
import org.objectweb.asm.tree.AbstractInsnNode

@DslMarker
annotation class PatternDslMarker

@DslMarker
annotation class StepDslMarker

@PatternDslMarker
fun instruction_pattern(init: InstructionPatternDsl.() -> Unit): InstructionPattern {
    val dsl = InstructionPatternDsl()
    dsl.init()
    return dsl.build()
}

class InstructionPatternDsl {

    private val steps = mutableListOf<Step>()

    @StepDslMarker
    fun wildcard(): WildcardStep {
        return WildcardStep().apply { steps.add(this) }
    }

    @StepDslMarker
    fun optional(vararg steps: Step): OptionalStep {
        return OptionalStep(*steps).apply { this@InstructionPatternDsl.steps.add(this) }
    }

    @StepDslMarker
    fun opcode(vararg opcodes: Int): Step {
        return OpcodeStep(*opcodes).apply { steps.add(this) }
    }

    @StepDslMarker
    fun check(predicate: (AbstractInsnNode) -> Boolean): CheckStep {
        return CheckStep(predicate).apply { steps.add(this) }
    }

    @StepDslMarker
    fun multi(vararg steps: Step): MultiStep {
        return MultiStep(*steps).apply { this@InstructionPatternDsl.steps.add(this) }
    }

    @StepDslMarker
    fun push_int(): PushIntStep {
        return PushIntStep().apply { steps.add(this) }
    }

    @StepDslMarker
    fun any(vararg steps: Step): OrStep {
        return OrStep(*steps).apply { this@InstructionPatternDsl.steps.add(this) }
    }

    fun Step.capture(name: String): Step {
        steps.remove(this)
        return CapturingStep(this, name).apply { steps.add(this) }
    }

    infix fun Step.and(other: Step): Step {
        steps.remove(this)
        return MultiStep(this, other).apply { steps.add(this) }
    }

    infix fun Step.or(other: Step): Step {
        steps.remove(this)
        return OrStep(this, other)
    }

    infix fun Step.and(predicate: (AbstractInsnNode) -> Boolean): Step {
        steps.remove(this)
        return MultiStep(this, CheckStep(predicate)).apply { steps.add(this) }
    }

    fun build(): InstructionPattern {
        return InstructionPattern(*steps.toTypedArray())
    }

}