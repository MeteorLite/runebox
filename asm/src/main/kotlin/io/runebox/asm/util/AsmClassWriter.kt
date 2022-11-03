package io.runebox.asm.util

import io.runebox.asm.tree.ClassPool
import org.objectweb.asm.ClassWriter

class AsmClassWriter(private val pool: ClassPool, flags: Int) : ClassWriter(flags) {

    private val classNames = pool.allClasses.associate { it.name to it }

    override fun getCommonSuperClass(type1: String, type2: String): String {
        return pool.inheritanceGraph.getCommon(type1, type2)
    }

}