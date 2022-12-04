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

package io.runebox.asm.transform

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import io.runebox.asm.classpath.ClassPath
import io.runebox.asm.classpath.Library
import io.runebox.asm.hasCode

abstract class Transformer {

    open fun transform(classPath: ClassPath) {
        preTransform(classPath)

        var changed: Boolean
        do {
            changed = prePass(classPath)

            for (library in classPath.libraries) {
                for (clazz in library) {
                    changed = changed or transformClass(classPath, library, clazz)

                    for (field in clazz.fields) {
                        changed = changed or transformField(classPath, library, clazz, field)
                    }

                    for (method in clazz.methods) {
                        changed = changed or preTransformMethod(classPath, library, clazz, method)

                        if (method.hasCode) {
                            changed = changed or transformCode(classPath, library, clazz, method)
                        }

                        changed = changed or postTransformMethod(classPath, library, clazz, method)
                    }
                }
            }

            changed = changed or postPass(classPath)
        } while (changed)

        postTransform(classPath)
    }

    protected open fun preTransform(classPath: ClassPath) {
        // empty
    }

    protected open fun prePass(classPath: ClassPath): Boolean {
        return false
    }

    protected open fun transformClass(classPath: ClassPath, library: Library, clazz: ClassNode): Boolean {
        return false
    }

    protected open fun transformField(
        classPath: ClassPath,
        library: Library,
        clazz: ClassNode,
        field: FieldNode
    ): Boolean {
        return false
    }

    protected open fun preTransformMethod(
        classPath: ClassPath,
        library: Library,
        clazz: ClassNode,
        method: MethodNode
    ): Boolean {
        return false
    }

    protected open fun transformCode(
        classPath: ClassPath,
        library: Library,
        clazz: ClassNode,
        method: MethodNode
    ): Boolean {
        return false
    }

    protected open fun postTransformMethod(
        classPath: ClassPath,
        library: Library,
        clazz: ClassNode,
        method: MethodNode
    ): Boolean {
        return false
    }

    protected open fun postPass(classPath: ClassPath): Boolean {
        return false
    }

    protected open fun postTransform(classPath: ClassPath) {
        // empty
    }
}
