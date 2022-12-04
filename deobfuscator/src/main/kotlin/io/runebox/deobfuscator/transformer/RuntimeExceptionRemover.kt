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

package io.runebox.deobfuscator.transformer

import io.runebox.asm.InsnMatcher
import io.runebox.asm.classpath.ClassPath
import io.runebox.asm.classpath.Library
import io.runebox.asm.nextReal
import io.runebox.asm.transform.Transformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.tinylog.kotlin.Logger

class RuntimeExceptionRemover : Transformer() {

    private var count = 0

    override fun transformCode(classPath: ClassPath, library: Library, clazz: ClassNode, method: MethodNode): Boolean {
        for(match in HANDLER_PATTERN.match(method)) {
            val foundTcb = method.tryCatchBlocks.removeIf { tcb ->
                tcb.type == "java/lang/RuntimeException" && tcb.handler.nextReal === match[0]
            }

            if(foundTcb) {
                match.forEach(method.instructions::remove)
                count++
            }

        }
        return false
    }

    override fun postTransform(classPath: ClassPath) {
        Logger.info("Removed $count 'RuntimeException' try-catch blocks.")
    }

    companion object {
        private val HANDLER_PATTERN = InsnMatcher.compile(
            """
                (NEW
                DUP
                INVOKESPECIAL
                LDC
                INVOKEVIRTUAL
                LDC
                INVOKEVIRTUAL
                INVOKEVIRTUAL
                INVOKESTATIC
                ATHROW)
            """
        )
    }
}