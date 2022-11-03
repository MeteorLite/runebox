package io.runebox.asm.analysis.rewriter;

import io.runebox.asm.analysis.rewriter.value.CodeReferenceValue;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.List;

/**
 * Same as {@link me.nov.threadtear.analysis.stack.IConstantReferenceHandler} for CodeRewriter
 */
public interface ICRReferenceHandler {

  Object getFieldValueOrNull(BasicValue v, String owner, String name, String desc);

  Object getMethodReturnOrNull(BasicValue v, String owner, String name, String desc,
                               List<? extends CodeReferenceValue> values);

}
