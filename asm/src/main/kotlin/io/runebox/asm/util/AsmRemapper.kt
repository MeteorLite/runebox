package io.runebox.asm.util

import io.runebox.asm.tree.*
import org.objectweb.asm.commons.Remapper

class AsmRemapper(val pool: ClassPool) : Remapper() {

    override fun map(typeName: String): String {
        val cls = pool.getClass(typeName) ?: return typeName
        return cls.mappedName
    }

    override fun mapFieldName(owner: String, name: String, desc: String): String {
        val cls = pool.getClass(owner) ?: return name
        val field = cls.resolveField(name, desc) ?: return name
        return field.mappedName
    }

    override fun mapMethodName(owner: String, name: String, desc: String): String {
        val cls = pool.getClass(owner) ?: return name
        val method = cls.getMethod(name, desc) ?: return name
        return method.mappedName
    }

    fun mapMethodName(owner: String, name: String, desc: String, itf: Boolean): String {
        val cls = pool.getClass(owner) ?: return name
        val method = cls.resolveMethod(name, desc) ?: return name
        return method.mappedName
    }

    fun mapArgName(owner: String, methodName: String, methodDesc: String, name: String, index: Int): String {
        val cls = pool.getClass(owner) ?: return name
        val method = cls.getMethod(methodName, methodDesc) ?: return name
        val arg = method.parameters?.get(index) ?: return name
        return arg.mappedName ?: name
    }
}