/*
 * Copyright (C) 2022 RuneBox <Kyle Escobar>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.runebox.deobfuscator.transformer

import io.runebox.asm.InsnMatcher
import io.runebox.asm.MemberRef
import io.runebox.asm.classpath.ClassPath
import io.runebox.asm.classpath.Library
import io.runebox.asm.transform.Transformer
import org.objectweb.asm.Opcodes.GOTO
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import org.tinylog.kotlin.Logger
import java.lang.reflect.Modifier

class OpaquePredicateRemover : Transformer() {

    private var count = 0

    override fun transformCode(classPath: ClassPath, library: Library, clazz: ClassNode, method: MethodNode): Boolean {
        for(match in EXCEPTION_PATTERN.match(method).filter { isOpaquePredicateException(method, it) }) {
            if(match[2] is JumpInsnNode) {
                val branch = match[2] as JumpInsnNode
                val label = branch.label
                val goto = JumpInsnNode(GOTO, label)
                method.instructions.insert(branch, goto)
            }
            match.forEach(method.instructions::remove)
            count++
        }

        for(match in RETURN_PATTERN.match(method).filter { isOpaquePredicateReturn(method, it) }) {
            if(match[2] is JumpInsnNode) {
                val branch = match[2] as JumpInsnNode
                val label = branch.label
                val goto = JumpInsnNode(GOTO, label)
                method.instructions.insert(branch, goto)
            }
            match.forEach(method.instructions::remove)
            count++
        }

        return false
    }

    override fun postTransform(classPath: ClassPath) {
        Logger.info("Removed $count opaque predicate checks.")
    }

    private fun isOpaquePredicateException(method: MethodNode, match: List<AbstractInsnNode>): Boolean {
        val load = match[0]
        if(load !is VarInsnNode || load.`var` != method.lastArgIndex) return false
        val invoke = match[5]
        if(invoke !is MethodInsnNode || invoke.owner != "java/lang/IllegalStateException") return false
        return true
    }

    private fun isOpaquePredicateReturn(method: MethodNode, match: List<AbstractInsnNode>): Boolean {
        val load = match[0]
        if(load !is VarInsnNode || load.`var` != method.lastArgIndex) return false
        return true
    }

    private val MethodNode.lastArgIndex: Int get() {
        val offset = if(Modifier.isStatic(access)) 1 else 0
        return (Type.getArgumentsAndReturnSizes(desc) shr 2) - offset - 1
    }

    private companion object {

        private val EXCEPTION_PATTERN = InsnMatcher.compile(
            """
                (ILOAD)
                (LDC | ICONST_M1 | ICONST_0 | ICONST_1 | ICONST_2 | ICONST_3 | ICONST_4 | ICONST_5 | SIPUSH | BIPUSH)
                (IF_ICMPEQ | IF_ICMPNE | IF_ICMPLT | IF_ICMPGT | IF_ICMPLE | IF_ICMPGE | IF_ACMPEQ | IF_ACMPNE)
                (NEW
                DUP
                INVOKESPECIAL
                ATHROW)
            """.trimIndent()
        )

        private val RETURN_PATTERN = InsnMatcher.compile(
            """
                (ILOAD)
                (LDC | ICONST_M1 | ICONST_0 | ICONST_1 | ICONST_2 | ICONST_3 | ICONST_4 | ICONST_5 | SIPUSH | BIPUSH)
                (IF_ICMPEQ | IF_ICMPNE | IF_ICMPLT | IF_ICMPGT | IF_ICMPLE | IF_ICMPGE | IF_ACMPEQ | IF_ACMPNE)
                (RETURN | ARETURN | DRETURN | FRETURN | IRETURN | LRETURN)
            """.trimIndent()
        )
    }
}