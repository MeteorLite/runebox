/*
 * Copyright (C) 2022 RuneBox <Kyle Escobar>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.runebox.asm

import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

object ClassForNameUtils {
    private val INVOKE_MATCHER = io.runebox.asm.InsnMatcher.Companion.compile("LDC INVOKESTATIC")

    private fun isClassForName(match: List<AbstractInsnNode>): Boolean {
        val ldc = match[0] as LdcInsnNode
        if (ldc.cst !is String) {
            return false
        }

        val invokestatic = match[1] as MethodInsnNode
        return invokestatic.owner == "java/lang/Class" &&
            invokestatic.name == "forName" &&
            invokestatic.desc == "(Ljava/lang/String;)Ljava/lang/Class;"
    }

    private fun findLdcInsns(method: MethodNode): Sequence<LdcInsnNode> {
        return io.runebox.asm.ClassForNameUtils.INVOKE_MATCHER.match(method)
            .filter(io.runebox.asm.ClassForNameUtils::isClassForName)
            .map { it[0] as LdcInsnNode }
    }

    private fun internalName(ldc: LdcInsnNode): String {
        return (ldc.cst as String).toInternalClassName()
    }

    fun findClassNames(method: MethodNode): Sequence<String> {
        return io.runebox.asm.ClassForNameUtils.findLdcInsns(method).map(io.runebox.asm.ClassForNameUtils::internalName)
    }

    fun remap(remapper: Remapper, method: MethodNode) {
        for (ldc in io.runebox.asm.ClassForNameUtils.findLdcInsns(method)) {
            val name = remapper.mapType(io.runebox.asm.ClassForNameUtils.internalName(ldc))
            if (name != null) {
                ldc.cst = name.toBinaryClassName()
            }
        }
    }
}
