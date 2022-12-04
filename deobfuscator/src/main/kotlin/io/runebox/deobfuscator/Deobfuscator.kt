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

import io.runebox.asm.classpath.ClassPath
import io.runebox.asm.classpath.Library
import io.runebox.asm.io.JarLibraryReader
import io.runebox.asm.io.JarLibraryWriter
import io.runebox.asm.transform.Transformer
import org.tinylog.kotlin.Logger
import java.io.File
import java.nio.file.Files
import kotlin.reflect.full.createInstance

object Deobfuscator {

    private lateinit var inputFile: File
    private lateinit var outputFile: File
    private var runTestClient = false

    private lateinit var classpath: ClassPath
    private val transformers = mutableListOf<Transformer>()

    @JvmStatic
    fun main(args: Array<String>) {
        if(args.size < 2) throw IllegalArgumentException("Usage: deobfuscator.jar <input-jar> <output-jar>")

        inputFile = File(args[0])
        outputFile = File(args[1])

        if(args.size == 3 && args[2] == "--test") {
            runTestClient = true
        }

        Logger.info("Loading classes from input jar: ${inputFile.name}.")
        val runtime = ClassLoader.getPlatformClassLoader()
        val gamepackLib = Library.read("gamepack", inputFile.toPath(), JarLibraryReader)
        classpath = ClassPath(
            runtime,
            emptyList(),
            listOf(gamepackLib)
        )
        Logger.info("Loaded ${classpath.libraryClasses.toList().size} classes from input jar.")

        init()
        run()

        Logger.info("Saving deobfuscated classes to jar: ${outputFile.name}.")

        if(outputFile.exists()) outputFile.deleteRecursively()
        Files.createFile(outputFile.toPath())

        gamepackLib.write(outputFile.absoluteFile.toPath(), JarLibraryWriter, classpath)

        Logger.info("Deobfuscator completed.")
        if(runTestClient) {
            TestClient(outputFile).start()
        }
    }

    private fun init() {
        Logger.info("Initializing.")

        /*
         * Register transformers
         */
        transformers.clear()

        Logger.info("Registered ${transformers.size} transformers.")
    }

    fun run() {
        Logger.info("Running deobfuscator.")

        Logger.info("Running deobfuscator transformers.")
        transformers.forEach { transformer ->
            Logger.info("Running transformer: ${transformer::class.simpleName}.")
            val start = System.currentTimeMillis()
            transformer.transform(classpath)
            val delta = System.currentTimeMillis() - start
            Logger.info("Finished transform in ${delta}ms.")
        }

        Logger.info("Finished running all transformers.")
    }

    private inline fun <reified T : Transformer> register() {
        transformers.add(T::class.createInstance())
    }

    fun String.isObfuscatedName(): Boolean {
        return this.length <= 3 && this !in arrayOf("run", "add")
    }
}