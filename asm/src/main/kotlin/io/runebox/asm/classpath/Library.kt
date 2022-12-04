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

import org.objectweb.asm.tree.ClassNode
import io.runebox.asm.io.LibraryReader
import io.runebox.asm.io.LibraryWriter
import org.openrs2.util.io.useAtomicOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.SortedMap
import java.util.TreeMap

class Library(val name: String) : Iterable<ClassNode> {
    private var classes: SortedMap<String, ClassNode> = TreeMap()

    constructor(name: String, library: Library) : this(name) {
        for (clazz in library.classes.values) {
            val copy = ClassNode()
            clazz.accept(copy)
            add(copy)
        }
    }

    operator fun contains(name: String): Boolean {
        return classes.containsKey(name)
    }

    operator fun get(name: String): ClassNode? {
        return classes[name]
    }

    fun add(clazz: ClassNode): ClassNode? {
        return classes.put(clazz.name, clazz)
    }

    fun remove(name: String): ClassNode? {
        return classes.remove(name)
    }

    override fun iterator(): Iterator<ClassNode> {
        return classes.values.iterator()
    }

    fun remap(remapper: ExtendedRemapper) {
        classes = LibraryRemapper(remapper, classes).remap()
    }

    fun write(path: Path, writer: LibraryWriter, classPath: ClassPath) {
        path.useAtomicOutputStream { output ->
            writer.write(output, classPath, classes.values)
        }
    }

    companion object {
        fun read(name: String, path: Path, reader: LibraryReader): Library {

            val classes = Files.newInputStream(path).use { input ->
                reader.read(input)
            }

            val library = Library(name)
            for (clazz in classes) {
                library.add(clazz)
            }
            return library
        }
    }
}
