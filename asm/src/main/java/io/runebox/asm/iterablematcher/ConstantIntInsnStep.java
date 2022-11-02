package io.runebox.asm.iterablematcher;

import io.runebox.asm.utils.Utils;
import org.objectweb.asm.tree.AbstractInsnNode;

public class ConstantIntInsnStep extends IterableStep<AbstractInsnNode> {

    private final Integer expectedConst;

    public ConstantIntInsnStep() {
        expectedConst = null;
    }

    public ConstantIntInsnStep(int expectedConst) {
        this.expectedConst = expectedConst;
    }

    @Override
    public boolean tryMatch(AbstractInsnNode ain) {
        return Utils.isInteger(ain) && (expectedConst == null || Utils.getIntValue(ain) == expectedConst);
    }
}
