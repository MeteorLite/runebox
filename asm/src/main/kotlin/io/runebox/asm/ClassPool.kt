package io.runebox.asm

import io.runebox.asm.context.CachingBloatContext
import io.runebox.asm.context.PersistentBloatContext
import io.runebox.asm.file.ClassFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class ClassPool {

    private val classMap = hashMapOf<String, ClassNode>()

    val loader = ClassPoolClassLoader(this)
    val context = PersistentBloatContext(loader)

    val classes get() = classMap.values.filter { !it.ignored }.toSet()
    val ignoredClasses get() = classMap.values.filter { it.ignored }.toSet()
    val allClasses get() = classMap.values.toSet()

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

    internal fun updateClass(node: ClassNode, data: ByteArray) {
        removeClass(node)
        addClass(data)
    }

    fun getClass(name: String) = classMap.filter { !it.value.ignored }[name]
    fun getIgnoredClass(name: String) = classMap.filter { it.value.ignored }[name]

    fun commit() {
        classes.forEach { it.commit() }
    }

    fun addJar(file: File) {
        JarFile(file).use { jar ->
            jar.entries().asSequence().forEach { entry ->
                if(entry.name.endsWith(".class")) {
                    val data = jar.getInputStream(entry).readAllBytes()
                    addClass(data)
                }
            }
        }
    }

    fun saveJar(file: File) {
        if(file.exists()) {
            file.deleteRecursively()
        }

        JarOutputStream(FileOutputStream(file)).use { jos ->
            classes.forEach { cls ->
                jos.putNextEntry(JarEntry("${cls.name}.class"))
                jos.write(cls.toByteArray())
                jos.closeEntry()
            }
        }
    }
}