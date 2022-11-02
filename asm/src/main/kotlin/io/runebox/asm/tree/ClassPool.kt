package io.runebox.asm.tree

import org.objectweb.asm.tree.ClassNode

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

    fun removeClass(node: ClassNode) {
        classMap.remove(node.name)
    }

    fun removeClass(name: String) {
        classMap.remove(name)
    }

    fun getClass(name: String) = classMap[name]
}