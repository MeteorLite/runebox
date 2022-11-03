package io.runebox.deobfuscator

import io.runebox.asm.ClassPool
import io.runebox.asm.tree.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException
import kotlin.reflect.full.createInstance

object Deobfuscator {

    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("deobfuscator")
        val inputJar by parser.argument(ArgType.String, "input-jar", "File path of the vanilla gamepack jar.")
        val outputJar by parser.argument(ArgType.String, "output-jar", "File path to save deobfuscated jar.")
        val runTestClient by parser.option(ArgType.Boolean, "test", "t", "Run a test client from the output jar.").default(false)
        parser.parse(args)
        run(File(inputJar), File(outputJar), runTestClient)
    }

    private lateinit var inputJar: File
    private lateinit var outputJar: File
    private var runTestClient = false

    private val pool = ClassPool()

    init {
    }

    fun run(inputJar: File, outputJar: File, runTestClient: Boolean = false) {
        Logger.info("Starting deobfuscator.")

        this.inputJar = inputJar
        this.outputJar = outputJar
        this.runTestClient = runTestClient

        if(!inputJar.exists()) {
            throw FileNotFoundException("Input jar (${inputJar.path}) does not exist.")
        }

        Logger.info("Deobfuscator complete. Exiting process.")
    }

    fun String.isObfuscatedName(): Boolean {
        return (this.length <= 3 && this !in arrayOf("run", "add", "put", "set", "get")) || (arrayOf("class", "method", "field").any { this.startsWith(it) })
    }
}