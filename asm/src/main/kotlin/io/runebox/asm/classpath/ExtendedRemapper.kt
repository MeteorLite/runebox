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

package io.runebox.asm.classpath

import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.tree.AbstractInsnNode

abstract class ExtendedRemapper : Remapper() {
    open fun getFieldInitializer(owner: String, name: String, descriptor: String): List<AbstractInsnNode>? {
        return null
    }

    open fun mapFieldOwner(owner: String, name: String, descriptor: String): String {
        return mapType(owner)
    }

    open fun mapMethodOwner(owner: String, name: String, descriptor: String): String {
        return mapType(owner)
    }

    open fun mapArgumentName(
        owner: String,
        name: String,
        descriptor: String,
        index: Int,
        argumentName: String?
    ): String? {
        return argumentName
    }
}
