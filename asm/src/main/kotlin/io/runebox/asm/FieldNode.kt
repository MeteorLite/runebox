package io.runebox.asm

import io.runebox.asm.editor.FieldEditor
import io.runebox.asm.file.Field
import io.runebox.asm.util.field
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode

internal fun FieldNode.init(owner: ClassNode) {
    this.owner = owner
}

var FieldNode.owner: ClassNode by field()
val FieldNode.pool get() = owner.pool

var FieldNode.info: Field by field()
var FieldNode.editor: FieldEditor by field()

fun FieldNode.commit() {
    editor.commit()
}