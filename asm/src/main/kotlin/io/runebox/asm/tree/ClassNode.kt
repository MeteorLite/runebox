package io.runebox.asm.tree

import io.runebox.asm.util.AsmClassWriter
import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.checkerframework.checker.units.qual.m2
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.util.CheckClassAdapter
import java.util.ArrayDeque

fun ClassNode.init(pool: ClassPool) {
    this.pool = pool
    mappedName = name
    methods.forEach { it.init(this) }
    fields.forEach { it.init(this) }
}

var ClassNode.pool: ClassPool by field()
var ClassNode.ignored: Boolean by field { false }
var ClassNode.mappedName: String by field()
val ClassNode.id get() = name
var ClassNode.obfId: String? by nullField()

var ClassNode.parent: ClassNode? by nullField()
val ClassNode.children: HashSet<ClassNode> by field { hashSetOf() }
val ClassNode.interfaceClasses: HashSet<ClassNode> by field { hashSetOf() }
val ClassNode.implementers: HashSet<ClassNode> by field { hashSetOf() }

fun ClassNode.getMethod(name: String, desc: String) = methods.firstOrNull { it.name == name && it.desc == desc }
fun ClassNode.getField(name: String, desc: String) = fields.firstOrNull { it.name == name && it.desc == desc }

val ClassNode.superClasses: Set<ClassNode> get() {
    return interfaceClasses.plus(parent)
        .filterNotNull()
        .flatMap { it.superClasses.plus(it) }
        .toSet()
}

val ClassNode.subClasses: Set<ClassNode> get() {
    return implementers.plus(children)
        .flatMap { it.subClasses.plus(it) }
        .toSet()
}

val ClassNode.hierarchy: Set<ClassNode> get() {
    return superClasses.plus(subClasses).plus(this).distinct().toSet()
}

fun ClassNode.resolveMethod(name: String, desc: String): MethodNode? {
    if(name.startsWith("<")) return getMethod(name, desc)
    for(sup in superClasses) {
        return sup.resolveMethod(name, desc) ?: continue
    }
    return getMethod(name, desc)
}

fun ClassNode.resolveField(name: String, desc: String): FieldNode? {
    for(sup in superClasses) {
        return sup.resolveField(name, desc) ?: continue
    }
    return getField(name, desc)
}

fun ClassNode.toByteArray(): ByteArray {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    this.accept(writer)
    val data = writer.toByteArray()
    //checkDataFlow(data)
    return data
}

private fun checkDataFlow(data: ByteArray) {
    try {
        val reader = ClassReader(data)
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_FRAMES)
        val checker = CheckClassAdapter(writer, true)
        reader.accept(checker, 0)
    } catch(e: Exception) {
        e.printStackTrace(System.err)
    }
}

fun ClassNode.copyOf(): ClassNode {
    val cls = ClassNode()
    cls.pool = pool
    cls.ignored = ignored
    cls.obfId = obfId
    cls.mappedName = mappedName
    return cls
}