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

package io.runebox.asm

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

data class MemberDesc(val name: String, val desc: String) {
    constructor(field: FieldNode) : this(field.name, field.desc)
    constructor(method: MethodNode) : this(method.name, method.desc)
    constructor(fieldInsn: FieldInsnNode) : this(fieldInsn.name, fieldInsn.desc)
    constructor(methodInsn: MethodInsnNode) : this(methodInsn.name, methodInsn.desc)
    constructor(memberRef: MemberRef) : this(memberRef.name, memberRef.desc)
    override fun toString(): String {
        return "$name $desc"
    }
}