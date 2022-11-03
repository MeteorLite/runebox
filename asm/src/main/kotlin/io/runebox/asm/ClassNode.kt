package io.runebox.asm


import io.runebox.asm.editor.ClassEditor
import io.runebox.asm.file.ClassFile
import io.runebox.asm.file.Field
import io.runebox.asm.file.Method
import io.runebox.asm.util.field
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.reflect.Modifier

internal fun ClassNode.init(pool: ClassPool, info: ClassFile) {
    this.pool = pool
    this.info = info
    editor = pool.context.editClass(info)

    methods.forEach { it.init(this) }
    fields.forEach { it.init(this) }

    /*
     * Build Method analysis editors
     */
    info.methods().forEach {
        val editor = pool.context.editMethod(it)
        val method = getMethod(editor.name(), editor.type().descriptor())!!
        method.editor = editor
        method.info = editor.methodInfo() as Method
    }

    /*
     * Build Field analysis editors.
     */
    info.fields().forEach {
        val editor = pool.context.editField(it)
        val field = getField(editor.name(), editor.type().descriptor())!!
        field.editor = editor
        field.info = editor.fieldInfo() as Field
    }
}

var ClassNode.pool: ClassPool by field()
var ClassNode.ignored: Boolean by field { false }
var ClassNode.info: ClassFile by field()
var ClassNode.editor: ClassEditor by field()

fun ClassNode.getMethod(name: String, desc: String) = methods.firstOrNull { it.name == name && it.desc == desc }
fun ClassNode.getField(name: String, desc: String) = fields.firstOrNull { it.name == name && it.desc == desc }

fun ClassNode.isInterface() = Modifier.isInterface(access)
fun ClassNode.isAbstract() = Modifier.isAbstract(access)

private var ClassNode.outputStream: OutputStream by field()
internal fun ClassNode.createOutputStream(): OutputStream {
    outputStream = ByteArrayOutputStream()
    return outputStream
}

fun ClassNode.commit() {
    methods.forEach { it.commit() }
    fields.forEach { it.commit() }
    editor.commit()
    info.commit()
    outputStream.close()
    pool.updateClass(this, (outputStream as ByteArrayOutputStream).toByteArray())
}

fun ClassNode.toByteArray(): ByteArray {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    this.accept(writer)
    return writer.toByteArray()
}