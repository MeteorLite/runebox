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

import io.runebox.asm.analysis.CopyPropagationAnalyzer
import io.runebox.asm.classpath.ClassPath
import io.runebox.asm.classpath.Library
import io.runebox.asm.transform.Transformer
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.DLOAD
import org.objectweb.asm.Opcodes.FLOAD
import org.objectweb.asm.Opcodes.ILOAD
import org.objectweb.asm.Opcodes.LLOAD
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import org.tinylog.kotlin.Logger

class CopyPropagationFixer : Transformer() {

    private var count = 0

    override fun transformCode(classPath: ClassPath, library: Library, clazz: ClassNode, method: MethodNode): Boolean {
        val analyzer = CopyPropagationAnalyzer(clazz.name, method)
        analyzer.analyze()

        for(insn in method.instructions) {
            if(insn !is VarInsnNode || insn.opcode !in LOAD_OPCODES) {
                continue
            }
            val set = analyzer.getInSet(insn) ?: continue
            val assignment = set.singleOrNull { it.destination == insn.`var` } ?: continue
            insn.`var` = assignment.source
            count++
        }
        return false
    }

    override fun postTransform(classPath: ClassPath) {
        Logger.info("Propagated $count local variable copies.")
    }

    companion object {
        private val LOAD_OPCODES = listOf(
            ILOAD,
            LLOAD,
            FLOAD,
            DLOAD,
            ALOAD
        )
    }
}