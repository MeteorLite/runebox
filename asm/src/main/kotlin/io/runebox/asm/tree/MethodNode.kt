package io.runebox.asm.tree

import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

fun MethodNode.init(owner: ClassNode) {
    this.owner = owner
}

var MethodNode.owner: ClassNode by field()
val MethodNode.pool get() = owner.pool
val MethodNode.id get() = "${owner.id}.$name$desc"
var MethodNode.obfId: String? by nullField()

fun MethodNode.isStatic() = (access and ACC_STATIC) != 0