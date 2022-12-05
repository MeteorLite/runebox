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

package io.runebox.deobfuscator

import io.runebox.asm.addJar
import io.runebox.asm.tree.ClassPool
import io.runebox.asm.tree.ignored
import io.runebox.asm.writeJar
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException
import kotlin.reflect.full.createInstance

object Deobfuscator {

    private val pool = ClassPool()
    private val transformers = mutableListOf<Transformer>()

    private var runTestClient = false

    @JvmStatic
    fun main(args: Array<String>) {
        if(args.size < 2) throw IllegalArgumentException("Usage: deobfuscator.jar <input_jar> <output_jar>")
        if(args.size == 3 && (args[2] == "--test" || args[2] == "-t")) {
            runTestClient = true
        }

        val inputJar = File(args[0])
        val outputJar = File(args[1])

        /*
         * Run the deobfuscator.
         */
        this.run(inputJar, outputJar)
    }

    fun run(inputJar: File, outputJar: File) {
        Logger.info("Preparing deobfuscator.")

        if(!inputJar.exists()) throw FileNotFoundException("Could not find input jar file: ${inputJar.path}.")
        transformers.clear()

        Logger.info("Loading classes from input jar: ${inputJar.name}.")
        pool.addJar(inputJar)
        pool.allClasses.forEach { cls ->
            if(!cls.name.isObfuscatedName() && cls.name != "client") {
                cls.ignored = true
            }
        }
        Logger.info("Successfully loaded ${pool.classes.size} classes into pool.")

        registerTransformers()

        Logger.info("Starting deobfuscator.")
        transformers.forEach { transformer ->
            Logger.info("Running transformer: ${transformer::class.simpleName}.")
            val start = System.currentTimeMillis()
            transformer.run(pool)
            val delta = System.currentTimeMillis() - start
            Logger.info("Finished transformer in ${delta}ms.")
        }

        Logger.info("Finished deobfuscator. Exporting classes to output jar: ${outputJar.name}.")
        if(outputJar.exists()) {
            outputJar.deleteRecursively()
        }
        pool.writeJar(outputJar)
        Logger.info("Successfully exported deobfuscated classes to output jar.")

        if(runTestClient) {
            Logger.info("Test client mode enabled. Loading jar file: ${outputJar.name}.")
            TestClient(outputJar).start()
            Logger.info("Exiting process.")
        }
    }

    private fun registerTransformers() {
        Logger.info("Registering bytecode transformers.")

        /*
         * Register bytecode transformers.
         */

        Logger.info("Successfully registered ${transformers.size} bytecode transformers.")
    }

    private inline fun <reified T : Transformer> register() {
        transformers.add(T::class.createInstance())
    }

    fun String.isObfuscatedName(): Boolean {
        return this.length <= 3 && (this !in arrayOf("run", "add")) || (arrayOf("class", "method", "field", "arg", "var").any { this.startsWith(it) })
    }
}