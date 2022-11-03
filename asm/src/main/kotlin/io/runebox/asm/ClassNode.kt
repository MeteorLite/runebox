package io.runebox.asm


import io.runebox.asm.editor.ClassEditor
import io.runebox.asm.file.ClassFile
import io.runebox.asm.reflect.ClassInfo
import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.OutputStream

internal fun ClassNode.init(pool: ClassPool, info: ClassFile) {
    this.pool = pool
    this.info = info
    editor = pool.context.editClass(info)
}

var ClassNode.pool: ClassPool by field()
var ClassNode.info: ClassFile by field()
var ClassNode.editor: ClassEditor by field()

private var ClassNode.outputStream: OutputStream by field()

internal fun ClassNode.createOutputStream(): OutputStream {
    outputStream = ByteArrayOutputStream()
    return outputStream
}

fun ClassNode.commit() {
    editor.commit()
    info.commit()
}

private fun ClassNode.update() {
    outputStream.close()
    pool.updateClass(this, (outputStream as ByteArrayOutputStream).toByteArray())
}

fun ClassNode.toByteArray(): ByteArray {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    this.accept(writer)
    return writer.toByteArray()
}