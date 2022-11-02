package io.runebox.asm.tree

import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode

fun FieldNode.init(owner: ClassNode) {
    this.owner = owner
}

var FieldNode.owner: ClassNode by field()
val FieldNode.pool get() = owner.pool
val FieldNode.id get() = "${owner.id}.$name"
var FieldNode.obfId: String? by nullField()

fun FieldNode.isStatic() = (access and ACC_STATIC) != 0