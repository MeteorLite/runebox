package io.runebox.asm.tree

import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

fun ClassNode.init(pool: ClassPool) {
    this.pool = pool
    methods.forEach { it.init(this) }
    fields.forEach { it.init(this) }
}

var ClassNode.pool: ClassPool by field()
var ClassNode.ignored by field { false }
val ClassNode.id get() = name
var ClassNode.obfId: String? by nullField()

fun ClassNode.getMethod(name: String, desc: String) = methods.firstOrNull { it.name == name && it.desc == desc }
fun ClassNode.getField(name: String, desc: String) = fields.firstOrNull { it.name == name && it.desc == desc }

val ClassNode.superClasses: Set<ClassNode> get() {
    return interfaces.plus(superName)
        .mapNotNull { pool.getClass(it) }
        .flatMap { it.superClasses.plus(it) }
        .toSet()
}

fun ClassNode.resolveMethod(name: String, desc: String): MethodNode? {
    for(superCls in superClasses) {
        return superCls.resolveMethod(name, desc) ?: continue
    }
    return getMethod(name, desc)
}

fun ClassNode.resolveField(name: String, desc: String): FieldNode? {
    for(superCls in superClasses) {
        return superCls.resolveField(name, desc) ?: continue
    }
    return getField(name, desc)
}

fun ClassNode.toByteArray(): ByteArray {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    accept(writer)
    return writer.toByteArray()
}

fun ClassNode.copyOf(): ClassNode {
    val cls = ClassNode()
    cls.ignored = ignored
    cls.obfId = obfId
    return cls
}