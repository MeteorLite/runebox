package io.runebox.asm.tree

import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import java.lang.reflect.Modifier

fun FieldNode.init(owner: ClassNode) {
    this.owner = owner
    mappedName = name
}

var FieldNode.owner: ClassNode by field()
val FieldNode.pool get() = owner.pool
val FieldNode.id get() = "${owner.id}.$name"
var FieldNode.obfId: String? by nullField()
var FieldNode.mappedName: String by field()

fun FieldNode.isStatic() = Modifier.isStatic(access)

val FieldNode.hierarchy: Set<FieldNode> get() {
    return owner.hierarchy
        .mapNotNull { it.getField(name, desc) }
        .toSet()
}