package io.runebox.asm

import io.runebox.asm.file.ClassFile
import io.runebox.asm.reflect.ClassInfo
import io.runebox.asm.reflect.ClassInfoLoader
import java.io.DataInputStream
import java.io.OutputStream

class ClassPoolClassLoader(private val pool: ClassPool) : ClassInfoLoader {

    override fun loadClass(name: String): ClassInfo {
        val cls = pool.getClass(name) ?: throw ClassNotFoundException("Class with name: $name not found in pool.")
        val bytes = cls.toByteArray()
        val dis = DataInputStream(bytes.inputStream())
        return ClassFile(null, this, dis)
    }

    override fun newClass(
        modifiers: Int,
        classIndex: Int,
        superClassIndex: Int,
        interfaceIndexes: IntArray?,
        constants: MutableList<Any?>?
    ): ClassInfo {
        return ClassFile(modifiers, classIndex, superClassIndex, interfaceIndexes, constants, this)
    }

    override fun outputStreamFor(info: ClassInfo): OutputStream {
        return pool.getClass(info.name())?.createOutputStream()
            ?: throw ClassNotFoundException("Class with name: ${info.name()} not found in pool.")
    }
}