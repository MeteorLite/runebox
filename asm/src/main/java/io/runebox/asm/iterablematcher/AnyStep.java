package io.runebox.asm.iterablematcher;

import io.runebox.asm.utils.Utils;
import org.objectweb.asm.tree.AbstractInsnNode;

public class AnyStep extends IterableStep<AbstractInsnNode> {

    @Override
    public boolean tryMatch(AbstractInsnNode ain) {
        return true;
    }

    @Override
    public String toString() {
        return "AnyStep{" +
               "captured=" + Utils.prettyprint(this.getCaptured()) +
               '}';
    }
}
