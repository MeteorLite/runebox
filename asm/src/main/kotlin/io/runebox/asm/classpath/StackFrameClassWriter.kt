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

import org.objectweb.asm.ClassWriter

class StackFrameClassWriter(private val classPath: ClassPath) : ClassWriter(COMPUTE_FRAMES) {
    override fun getCommonSuperClass(type1: String, type2: String): String {
        var c = classPath[type1]!!
        val d = classPath[type2]!!
        return when {
            c.isAssignableFrom(d) -> type1
            d.isAssignableFrom(c) -> type2
            c.`interface` || d.`interface` -> "java/lang/Object"
            else -> {
                do {
                    c = c.superClass!!
                } while (!c.isAssignableFrom(d))
                c.name
            }
        }
    }
}
