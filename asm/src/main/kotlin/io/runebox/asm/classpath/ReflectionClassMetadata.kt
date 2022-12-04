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

import org.objectweb.asm.Type
import io.runebox.asm.MemberDesc
import io.runebox.asm.toInternalClassName

private val Class<*>.asmName: String
    get() = name.toInternalClassName()

class ReflectionClassMetadata(private val classPath: ClassPath, private val clazz: Class<*>) : ClassMetadata() {
    override val name: String
        get() = clazz.asmName

    override val dependency: Boolean
        get() = true

    override val `interface`: Boolean
        get() = clazz.isInterface

    override val superClass: ClassMetadata?
        get() = if (clazz.superclass != null) classPath[clazz.superclass.asmName]!! else null

    override val superInterfaces: List<ClassMetadata>
        get() = clazz.interfaces.map { classPath[it.asmName]!! }

    override val fields: List<MemberDesc>
        get() = clazz.declaredFields.map { MemberDesc(it.name, Type.getDescriptor(it.type)) }

    override val methods: List<MemberDesc>
        get() = clazz.declaredMethods.map { MemberDesc(it.name, Type.getMethodDescriptor(it)) }

    override fun getFieldAccess(field: MemberDesc): Int? {
        return clazz.declaredFields.find { it.name == field.name && Type.getDescriptor(it.type) == field.desc }
            ?.modifiers
    }

    override fun getMethodAccess(method: MemberDesc): Int? {
        return clazz.declaredMethods.find { it.name == method.name && Type.getMethodDescriptor(it) == method.desc }
            ?.modifiers
    }
}
