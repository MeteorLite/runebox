package io.runebox.asm.matcher;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.function.Function;

public class CheckStep implements Step {

    private final Function<AbstractInsnNode, Boolean> check;

    public CheckStep() {
        this(null);
    }

    public CheckStep(Function<AbstractInsnNode, Boolean> check) {
        this.check = check;
    }


    @Override
    public AbstractInsnNode tryMatch(InstructionMatcher matcher, AbstractInsnNode now) {
        if(this.check == null || this.check.apply(now)) {
            return now.getNext();
        }
        return null;
    }
}
