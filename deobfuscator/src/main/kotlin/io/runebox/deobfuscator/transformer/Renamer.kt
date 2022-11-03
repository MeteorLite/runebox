package io.runebox.deobfuscator.transformer

import io.runebox.asm.tree.*
import io.runebox.asm.util.AsmClassRemapper
import io.runebox.asm.util.AsmRemapper
import io.runebox.deobfuscator.Deobfuscator.isObfuscatedName
import io.runebox.deobfuscator.Transformer
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import org.tinylog.kotlin.Logger

class Renamer : Transformer {

    private var classCount = 0
    private var methodCount = 0
    private var fieldCount = 0

    private val mappings = hashMapOf<String, String>()

    override fun run(pool: ClassPool) {
        generateMappings(pool)
        applyMappings(pool)

        Logger.info("Renamed $classCount classes.")
        Logger.info("Renamed $methodCount methods.")
        Logger.info("Renamed $fieldCount fields.")
    }

    private fun generateMappings(pool: ClassPool) {
        pool.classes.forEach classLoop@ { cls ->
            if(cls.name.isObfuscatedName() && cls.name.lowercase() != "client") {
                val newName = "class${++classCount}"
                mappings[cls.id] = newName
            }
        }

        pool.classes.forEach { cls ->
            cls.methods.forEach methodLoop@ { method ->
                if(method.name.isObfuscatedName() && !mappings.containsKey(method.id)) {
                    val newName = "method${++methodCount}"
                    mappings[method.id] = newName
                    pool.inheritanceGraph.getAllChildren(cls.name).forEach { child ->
                        mappings["$child.${method.name}${method.desc}"] = newName
                    }
                }
            }
        }

        pool.classes.forEach { cls ->
            cls.fields.forEach fieldLoop@ { field ->
                if(field.name.isObfuscatedName() && !mappings.containsKey(field.id)) {
                    val newName = "field${++fieldCount}"
                    mappings[field.id] = newName
                    pool.inheritanceGraph.getAllChildren(cls.name).forEach { child ->
                        mappings["$child.${field.name}"] = newName
                    }
                }
            }
        }
    }

    private fun applyMappings(pool: ClassPool) {
        val remapper = SimpleRemapper(mappings)
        val newClasses = mutableListOf<ClassNode>()

        pool.allClasses.forEach { cls ->
            val newCls = ClassNode()
            cls.accept(ClassRemapper(newCls, remapper))
            newCls.ignored = cls.ignored
            newClasses.add(newCls)
        }

        pool.clear()
        newClasses.forEach { pool.addClass(it) }
        pool.buildHierarchy()
    }
}