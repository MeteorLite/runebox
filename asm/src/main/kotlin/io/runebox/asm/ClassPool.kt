package io.runebox.asm

import io.runebox.asm.common.ClassPoolClassLoader
import io.runebox.asm.context.CachingBloatContext
import io.runebox.asm.file.ClassFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

class ClassPool {

    private val classMap = hashMapOf<String, ClassNode>()

    val loader = ClassPoolClassLoader(this)
    val context = CachingBloatContext(loader, mutableListOf<ClassFile>(), false)

    val classes get() = classMap.values.toSet()

    fun addClass(node: ClassNode) {
        classMap[node.name] = node
        val info = loader.loadClass(node.name) as ClassFile
        node.init(this, info)
    }

    fun addClass(data: ByteArray) {
        val reader = ClassReader(data)
        val node = ClassNode()
        reader.accept(node, ClassReader.SKIP_FRAMES)
        addClass(node)
    }

    fun removeClass(node: ClassNode) {
        classMap.remove(node.name)
    }

    fun updateClass(node: ClassNode, data: ByteArray) {
        removeClass(node)
        addClass(data)
    }

    fun getClass(name: String) = classMap[name]

}