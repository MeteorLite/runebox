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

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.util.CheckClassAdapter
import io.runebox.asm.ClassVersionUtils
import io.runebox.asm.NopClassVisitor
import io.runebox.asm.classpath.ClassPath
import io.runebox.asm.classpath.StackFrameClassWriter
import java.io.OutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

abstract class AbstractJarLibraryWriter : LibraryWriter {
    override fun write(output: OutputStream, classPath: ClassPath, classes: Iterable<ClassNode>) {
        createJarOutputStream(output).use { jar ->
            for (clazz in classes) {
                val writer = if (ClassVersionUtils.gte(clazz.version, Opcodes.V1_7)) {
                    StackFrameClassWriter(classPath)
                } else {
                    ClassWriter(ClassWriter.COMPUTE_MAXS)
                }

                clazz.accept(writer)

                jar.putNextEntry(JarEntry(clazz.name + CLASS_SUFFIX))
                jar.write(writer.toByteArray())

                /*
                 * XXX(gpe): CheckClassAdapter breaks the Label offset
                 * calculation in the OriginalPcTable's write method, so we do
                 * a second pass without any attributes to check the class,
                 * feeding the callbacks into a no-op visitor.
                 */
                for (method in clazz.methods) {
                    method.attrs?.clear()
                }
                clazz.accept(CheckClassAdapter(NopClassVisitor, true))
            }
        }
    }

    protected abstract fun createJarOutputStream(output: OutputStream): JarOutputStream

    private companion object {
        private const val CLASS_SUFFIX = ".class"
    }
}
