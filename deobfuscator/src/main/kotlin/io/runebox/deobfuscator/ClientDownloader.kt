package io.runebox.deobfuscator

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import org.tinylog.kotlin.Logger
import java.io.File
import java.net.URL

object ClientDownloader {

    private const val DOWNLOAD_URL = "http://oldschool1.runescape.com/gamepack.jar"

    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("client-downloader")
        val dir by parser.argument(ArgType.String, "output-dir", "Directory to save download client jar in.")
        parser.parse(args)

        run(File(dir))
    }

    fun run(dir: File) {
        if(!dir.exists()) {
            dir.mkdirs()
        }

        val file = dir.resolve("gamepack.jar")
        if(file.exists()) {
            file.deleteRecursively()
        }

        Logger.info("Downloading latest Old School RuneScape client from Jagex's servers.")

        val gamepackBytes = URL(DOWNLOAD_URL).readBytes()
        file.outputStream().use { output ->
            output.write(gamepackBytes)
        }

        Logger.info("Gamepack jar has successfully been downloaded.")
    }
}