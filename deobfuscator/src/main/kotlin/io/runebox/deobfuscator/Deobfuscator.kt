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

import io.runebox.asm.Asm
import io.runebox.asm.tree.ClassPool
import io.runebox.asm.tree.ignored
import io.runebox.deobfuscator.transformer.AnnotationClassAdder
import io.runebox.deobfuscator.transformer.DeadCodeRemover
import io.runebox.deobfuscator.transformer.RuntimeExceptionRemover
import org.tinylog.kotlin.Logger
import java.io.File
import kotlin.reflect.full.createInstance

object Deobfuscator {

    private lateinit var inputJar: File
    private lateinit var outputJar: File
    private var runTestClient = false

    private lateinit var pool: ClassPool

    private val transformers = mutableListOf<Transformer>()

    @JvmStatic
    fun main(args: Array<String>) {
        if(args.size < 2) throw IllegalArgumentException("Usage: deobfuscator.jar <input-jar> <output-jar>")

        inputJar = File(args[0])
        outputJar = File(args[1])

        if(args.size == 3 && args[2] == "--test") {
            runTestClient = true
        }

        pool = Asm.readJar(inputJar)
        pool.allClasses.forEach { cls ->
            if(!cls.name.isObfuscatedName()) {
                cls.ignored = true
            }
        }
        pool.buildInheritanceGraph()
        Logger.info("Loaded ${pool.classes.size} classes from jar: ${inputJar.name}.")

        init()
        run()

        Logger.info("Saving deobfuscated classes to jar: ${outputJar.name}.")
        Asm.writeJar(outputJar, pool)

        Logger.info("Deobfuscator completed.")

        if(runTestClient) {
            TestClient(outputJar).start()
        }
    }

    private fun init() {
        Logger.info("Initializing.")

        /*
         * Register transformers
         */
        transformers.clear()

        addTransformer<AnnotationClassAdder>()
        addTransformer<RuntimeExceptionRemover>()
        addTransformer<DeadCodeRemover>()

        Logger.info("Registered ${transformers.size} transformers.")
    }

    fun run() {
        Logger.info("Running deobfuscator.")

        Logger.info("Running deobfuscator transformers.")
        transformers.forEach { transformer ->
            Logger.info("Running transformer: ${transformer::class.simpleName}.")
            val start = System.currentTimeMillis()
            transformer.run(pool)
            val delta = System.currentTimeMillis() - start
            Logger.info("Finished transform in ${delta}ms.")
        }

        Logger.info("Finished running all transformers.")
    }

    private inline fun <reified T : Transformer> addTransformer() {
        transformers.add(T::class.createInstance())
    }

    fun String.isObfuscatedName(): Boolean {
        return this.length <= 3 && this !in arrayOf("run", "add")
    }
}