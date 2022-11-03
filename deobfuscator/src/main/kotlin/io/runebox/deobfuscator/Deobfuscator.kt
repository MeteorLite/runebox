package io.runebox.deobfuscator

import io.runebox.asm.tree.*
import io.runebox.deobfuscator.transformer.*
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
    private val transformers = mutableListOf<Transformer>()

    init {
        transform<RuntimeExceptionRemover>()
        transform<DeadCodeRemover>()
        transform<ControlFlowFixer>()
        transform<Renamer>()
        transform<OpaquePredicateRemover>()
        transform<MultiplierRemover>()
    }

    fun run(inputJar: File, outputJar: File, runTestClient: Boolean = false) {
        Logger.info("Starting deobfuscator.")

        this.inputJar = inputJar
        this.outputJar = outputJar
        this.runTestClient = runTestClient

        if(!inputJar.exists()) {
            throw FileNotFoundException("Input jar (${inputJar.path}) does not exist.")
        }

        pool.addJar(inputJar)
        pool.ignoreClasses {
            it.name.contains("bouncycastle") ||
            it.name.contains("json")
        }
        pool.buildHierarchy()
        Logger.info("Loaded ${pool.classes.size} classes from input jar file.")

        /*
         * Run bytecode transformers.
         */
        Logger.info("Running ${transformers.size} bytecode transforms on classes.")
        transformers.forEach { transformer ->
            Logger.info("Running transformer: '${transformer::class.simpleName}'.")
            val start = System.currentTimeMillis()
            transformer.run(pool)
            Logger.info("Finished transform in ${System.currentTimeMillis() - start}ms.")
        }
        Logger.info("Successfully ran all bytecode transforms.")

        Logger.info("Saving deobfuscated classes to output jar.")
        if(outputJar.exists()) outputJar.deleteRecursively()
        pool.writeJar(outputJar)
        Logger.info("Succesfully saved output jar.")

        if(runTestClient) {
            Logger.info("Starting test client using output jar.")
            TestClient(outputJar, inputJar).start()
        }

        Logger.info("Deobfuscator complete. Exiting process.")
    }

    private inline fun <reified T : Transformer> transform() {
        transformers.add(T::class.createInstance())
    }

    fun String.isObfuscatedName(): Boolean {
        return (this.length <= 3 && this !in arrayOf("run", "add", "put", "set", "get")) || (arrayOf("class", "method", "field").any { this.startsWith(it) })
    }
}