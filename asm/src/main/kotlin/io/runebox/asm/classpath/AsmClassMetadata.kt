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

package io.runebox.asm.classpath

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import io.runebox.asm.MemberDesc

class AsmClassMetadata(
    private val classPath: ClassPath,
    private val clazz: ClassNode,
    override val dependency: Boolean
) : ClassMetadata() {
    override val name: String
        get() = clazz.name

    override val `interface`: Boolean
        get() = clazz.access and Opcodes.ACC_INTERFACE != 0

    override val superClass: ClassMetadata?
        get() = clazz.superName?.let { classPath[it] ?: error("Failed to find $it on provided classpath.") }

    override val superInterfaces: List<ClassMetadata>
        get() = clazz.interfaces.map { classPath[it] ?: error("Failed to find $it on provided classpath.") }

    override val fields: List<MemberDesc>
        get() = clazz.fields.map(::MemberDesc)

    override val methods: List<MemberDesc>
        get() = clazz.methods.map(::MemberDesc)

    override fun getFieldAccess(field: MemberDesc): Int? {
        return clazz.fields.find { it.name == field.name && it.desc == field.desc }?.access
    }

    override fun getMethodAccess(method: MemberDesc): Int? {
        return clazz.methods.find { it.name == method.name && it.desc == method.desc }?.access
    }
}
