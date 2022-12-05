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

package io.runebox.asm.tree

import org.objectweb.asm.tree.ClassNode

class ClassPool {

    private val classMap = hashSetOf<ClassNode>()

    val classes get() = classMap.filter { !it.ignored }.toList()
    val ignoredClasses get() = classMap.filter { it.ignored }.toList()
    val allClasses get() = classMap.toList()

    fun addClass(node: ClassNode) {
        classMap.add(node)
        node.init(this)
    }

    fun removeClass(node: ClassNode) {
        classMap.remove(node)
    }

    fun getClass(name: String) = classMap.filter { !it.ignored }.firstOrNull { it.name == name }
    fun getIgnoredClass(name: String) = classMap.filter { it.ignored }.firstOrNull { it.name == name }
    fun findClass(name: String) = classMap.firstOrNull { it.name == name }

    fun clear() {
        classMap.clear()
    }

    fun init(block: (ClassPool) -> Unit = {}) {
        block(this)
    }

}