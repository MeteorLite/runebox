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

package io.runebox.asm.io

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import io.runebox.asm.classpath.JsrInliner
import org.openrs2.util.io.entries
import java.io.InputStream
import java.util.jar.JarInputStream

object JarLibraryReader : LibraryReader {
    private const val CLASS_SUFFIX = ".class"

    override fun read(input: InputStream): Iterable<ClassNode> {
        val classes = mutableListOf<ClassNode>()

        JarInputStream(input).use { jar ->
            for (entry in jar.entries) {
                if (!entry.name.endsWith(CLASS_SUFFIX)) {
                    continue
                }

                val clazz = ClassNode()
                val reader = ClassReader(jar)
                reader.accept(JsrInliner(clazz), ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

                classes += clazz
            }
        }

        return classes
    }
}
