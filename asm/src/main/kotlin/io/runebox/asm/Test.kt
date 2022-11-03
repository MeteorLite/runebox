package io.runebox.asm

import java.io.File
import java.util.jar.JarFile

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val pool = ClassPool()
        val jarFile = File("build/workspace/gamepack.jar")
        JarFile(jarFile).use { jar ->
            jar.entries().asSequence().forEach { entry ->
                if(entry.name.endsWith(".class")) {
                    pool.addClass(jar.getInputStream(entry).readAllBytes())
                }
            }
        }

        println("Loaded ${pool.classes.size} classes.")
    }
}