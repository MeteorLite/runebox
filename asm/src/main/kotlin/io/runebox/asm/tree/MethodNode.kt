package io.runebox.asm.tree

import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.lang.reflect.Modifier

fun MethodNode.init(owner: ClassNode) {
    this.owner = owner
    mappedName = name
}

var MethodNode.owner: ClassNode by field()
val MethodNode.pool get() = owner.pool
val MethodNode.id get() = "${owner.id}.$name$desc"
var MethodNode.obfId: String? by nullField()
var MethodNode.mappedName: String by field()

fun MethodNode.isStatic() = Modifier.isStatic(access)
fun MethodNode.isAbstract() = Modifier.isAbstract(access)

val MethodNode.type get() = Type.getMethodType(desc)
val MethodNode.argumentTypes get() = type.argumentTypes
val MethodNode.returnType get() = type.returnType

val MethodNode.hierarchy: Set<MethodNode> get() {
    return owner.hierarchy
        .mapNotNull { it.getMethod(name, desc) }
        .toSet()
}