package io.runebox.asm.tree

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class ClassPool {

    private val classMap = hashMapOf<String, ClassNode>()

    val classes get() = classMap.values.filter { !it.ignored }.toSet()
    val ignoredClasses get() = classMap.values.filter { it.ignored }.toSet()
    val allClasses get() = classMap.values.toSet()

    fun ignoreClasses(predicate: (ClassNode) -> Boolean) {
        allClasses.forEach { cls ->
            if(predicate(cls)) {
                cls.ignored = true
            }
        }
    }

    fun addClass(node: ClassNode) {
        classMap[node.name] = node
        node.init(this)
    }

    fun addClass(data: ByteArray) {
        val cls = ClassNode()
        val reader = ClassReader(data)
        reader.accept(cls, ClassReader.EXPAND_FRAMES)
        addClass(cls)
    }

    fun removeClass(node: ClassNode) {
        classMap.remove(node.name)
    }

    fun removeClass(name: String) {
        classMap.remove(name)
    }

    fun getClass(name: String) = classMap[name]

    fun addJar(file: File) {
        JarFile(file).use { jar ->
            jar.entries().asSequence().forEach { entry ->
                if(entry.name.endsWith(".class")) {
                    addClass(jar.getInputStream(entry).readAllBytes())
                }
            }
        }
    }

    fun writeJar(file: File) {
        if(file.exists()) file.deleteRecursively()
        JarOutputStream(FileOutputStream(file)).use { jos ->
            allClasses.forEach { cls ->
                jos.putNextEntry(JarEntry("${cls.name}.class"))
                jos.write(cls.toByteArray())
                jos.closeEntry()
            }
        }
    }

    fun clear() {
        classMap.clear()
    }
}