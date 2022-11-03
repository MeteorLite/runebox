package io.runebox.asm.vm;

import org.objectweb.asm.tree.ClassNode;

public interface IVMReferenceHandler {
  ClassNode tryClassLoad(String name);
}
