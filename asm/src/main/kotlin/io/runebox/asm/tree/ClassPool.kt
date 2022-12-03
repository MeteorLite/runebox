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

import io.runebox.asm.Asm
import io.runebox.asm.util.InheritanceGraph
import org.objectweb.asm.tree.ClassNode

class ClassPool {

    private val classMap = hashMapOf<String, ClassNode>()

    lateinit var inheritanceGraph: InheritanceGraph private set

    val classes get() = classMap.values.filter { !it.ignored }.toList()
    val ignoredClasses get() = classMap.values.filter { it.ignored }.toList()
    val allClasses get() = classMap.values.toList()

    fun addClass(data: ByteArray) {
        val node = Asm.readClass(data)
        addClass(node)
    }

    fun addClass(node: ClassNode) {
        classMap[node.name] = node
        node.init(this)
    }

    fun removeClass(name: String) {
        classMap.remove(name)
    }

    fun removeClass(node: ClassNode) {
        classMap.remove(node.name)
    }

    fun getClass(name: String) = classMap.filterValues { !it.ignored }[name]
    fun getIgnoredClass(name: String) = classMap.filterValues { it.ignored }[name]
    fun findClass(name: String) = getClass(name) ?: getIgnoredClass(name)

    fun clear() {
        classMap.clear()
    }

    fun buildInheritanceGraph() {
        inheritanceGraph = InheritanceGraph(this)
    }

}