package io.runebox.asm.util

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.MethodRemapper

class AsmClassRemapper(cv: ClassVisitor, val asmRemapper: AsmRemapper) : ClassRemapper(cv, asmRemapper) {

    private lateinit var methodName: String
    private lateinit var methodDesc: String

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        methodName = name
        methodDesc = descriptor
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun createMethodRemapper(mv: MethodVisitor): MethodVisitor {
        return AsmMethodRemapper(mv, asmRemapper)
    }

    private class AsmMethodRemapper(val mv: MethodVisitor, val asmRemapper: AsmRemapper) : MethodRemapper(mv, asmRemapper) {

        override fun visitMethodInsn(
            opcode: Int,
            owner: String,
            name: String,
            descriptor: String,
            isInterface: Boolean
        ) {
            super.visitMethodInsn(
                opcode,
                asmRemapper.mapType(owner),
                asmRemapper.mapMethodName(owner, name, descriptor, isInterface),
                asmRemapper.mapMethodDesc(descriptor),
                isInterface
            )
        }
    }
}