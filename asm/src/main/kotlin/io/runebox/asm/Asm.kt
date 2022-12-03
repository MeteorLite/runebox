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

package io.runebox.asm

import io.runebox.asm.tree.ClassPool
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.ATHROW
import org.objectweb.asm.Opcodes.GOTO
import org.objectweb.asm.Opcodes.IRETURN
import org.objectweb.asm.Opcodes.LOOKUPSWITCH
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.Opcodes.TABLESWITCH
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FrameNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LineNumberNode
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

object Asm {

    fun readClass(data: ByteArray): ClassNode {
        val node = ClassNode()
        val reader = ClassReader(data)
        reader.accept(node, ClassReader.EXPAND_FRAMES)
        return node
    }

    fun writeClass(node: ClassNode): ByteArray {
        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
        node.accept(writer)
        return writer.toByteArray()
    }

    fun readJar(file: File): ClassPool {
        val pool = ClassPool()
        JarFile(file).use { jar ->
            jar.entries().asSequence().forEach { entry ->
                if(!entry.name.endsWith(".class")) return@forEach
                val data = jar.getInputStream(entry).readAllBytes()
                val node = readClass(data)
                pool.addClass(node)
            }
        }
        return pool
    }

    fun writeJar(file: File, pool: ClassPool) {
        if(file.exists()) file.deleteRecursively()
        JarOutputStream(FileOutputStream(file)).use { jos ->
            pool.allClasses.forEach { cls ->
                jos.putNextEntry(JarEntry(cls.name+".class"))
                jos.write(writeClass(cls))
                jos.closeEntry()
            }
        }
    }

    fun AbstractInsnNode.isInstruction() = this !is LineNumberNode && this !is FrameNode && this !is LabelNode

    fun AbstractInsnNode.next(amount: Int): AbstractInsnNode {
        var cur = this
        repeat(amount) {
            cur = cur.next
        }
        return cur
    }

    fun AbstractInsnNode.previous(amount: Int): AbstractInsnNode {
        var cur = this
        repeat(amount) {
            cur = cur.previous
        }
        return cur
    }

    fun AbstractInsnNode.isTerminal() = when(this.opcode) {
        in IRETURN..RETURN -> true
        ATHROW -> true
        TABLESWITCH -> true
        LOOKUPSWITCH -> true
        GOTO -> true
        else -> false
    }

    fun InsnList.copy(): InsnList {
        val newInsns = InsnList()
        var insn = this.first
        while(insn != null) {
            newInsns.add(insn)
            insn = insn.next
        }
        return newInsns
    }

}