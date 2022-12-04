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

import io.runebox.asm.classpath.ClassPath
import io.runebox.asm.classpath.Library
import io.runebox.asm.nextReal
import io.runebox.asm.removeDeadCode
import io.runebox.asm.transform.Transformer
import org.objectweb.asm.Opcodes.GOTO
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.MethodNode
import org.tinylog.kotlin.Logger

class RedundantGotoRemover : Transformer() {

    private var count = 0

    override fun preTransformMethod(
        classPath: ClassPath,
        library: Library,
        clazz: ClassNode,
        method: MethodNode
    ): Boolean {
        method.removeDeadCode(clazz.name)
        return false
    }

    override fun transformCode(classPath: ClassPath, library: Library, clazz: ClassNode, method: MethodNode): Boolean {
        for(insn in method.instructions) {
            if(insn.opcode == GOTO) {
                insn as JumpInsnNode
                if(insn.nextReal === insn.label.nextReal) {
                    method.instructions.remove(insn)
                    count++
                }
            }
        }
        return false
    }

    override fun postTransform(classPath: ClassPath) {
        Logger.info("Removed $count redundant GOTO instructions.")
    }
}