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

package org.openrs2.util.io

import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.CopyOption
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.nio.file.attribute.FileAttribute

fun Path.fsync() {
    require(Files.isRegularFile(this) || Files.isDirectory(this))

    FileChannel.open(this, StandardOpenOption.READ).use { channel ->
        channel.force(true)
    }
}

fun Path.recursiveCopy(
    destination: Path,
    visitOptions: Array<FileVisitOption> = emptyArray(),
    copyOptions: Array<CopyOption> = emptyArray()
) {
    for (sourceFile in Files.walk(this, *visitOptions)) {
        val destinationFile = destination.resolve(this.relativize(sourceFile).toString())

        if (Files.isDirectory(sourceFile)) {
            Files.createDirectory(destinationFile)
        } else {
            Files.copy(sourceFile, destinationFile, *copyOptions)
        }
    }
}

fun Path.recursiveEquals(
    other: Path,
    linkOptions: Array<LinkOption> = emptyArray(),
    openOptions: Array<OpenOption> = emptyArray(),
    filter: (Path) -> Boolean = { true }
): Boolean {
    val list1 = Files.newDirectoryStream(this).use { stream ->
        stream.filter(filter).map { this.relativize(it).toString() }.sorted()
    }

    val list2 = Files.newDirectoryStream(other).use { stream ->
        stream.filter(filter).map { other.relativize(it).toString() }.sorted()
    }

    if (list1 != list2) {
        return false
    }

    for (file in list1) {
        val file1 = this.resolve(file)
        val file2 = other.resolve(file)

        if (Files.isDirectory(file1, *linkOptions)) {
            if (!Files.isDirectory(file2, *linkOptions)) {
                return false
            }
        } else {
            Files.newInputStream(file1, *openOptions).use { input1 ->
                Files.newInputStream(file2, *openOptions).use { input2 ->
                    if (!input1.contentEquals(input2)) {
                        return false
                    }
                }
            }
        }
    }

    return true
}

inline fun <T> useTempFile(
    prefix: String? = null,
    suffix: String? = null,
    vararg attributes: FileAttribute<*>,
    f: (Path) -> T
): T {
    val tempFile = Files.createTempFile(prefix, suffix, *attributes)
    try {
        return f(tempFile)
    } finally {
        Files.deleteIfExists(tempFile)
    }
}

inline fun <T> Path.useTempFile(
    prefix: String? = null,
    suffix: String? = null,
    vararg attributes: FileAttribute<*>,
    f: (Path) -> T
): T {
    val tempFile = Files.createTempFile(this, prefix, suffix, *attributes)
    try {
        return f(tempFile)
    } finally {
        Files.deleteIfExists(tempFile)
    }
}

inline fun <T> Path.atomicWrite(
    sync: Boolean = true,
    f: (Path) -> T
): T {
    parent.useTempFile(".$fileName", ".tmp") { tempFile ->
        val result = f(tempFile)
        if (sync) {
            tempFile.fsync()
        }

        Files.move(tempFile, this, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)

        if (sync) {
            try {
                parent.fsync()
            } catch (ex: IOException) {
                // can't fsync directories on (at least) Windows and jimfs
            }
        }

        return result
    }
}

inline fun <T> Path.useAtomicBufferedWriter(
    vararg options: OpenOption,
    sync: Boolean = true,
    f: (BufferedWriter) -> T
): T {
    return atomicWrite(sync) { path ->
        Files.newBufferedWriter(path, *options).use { writer ->
            f(writer)
        }
    }
}

inline fun <T> Path.useAtomicBufferedWriter(
    cs: Charset,
    vararg options: OpenOption,
    sync: Boolean = true,
    f: (BufferedWriter) -> T
): T {
    return atomicWrite(sync) { path ->
        Files.newBufferedWriter(path, cs, *options).use { writer ->
            f(writer)
        }
    }
}

inline fun <T> Path.useAtomicOutputStream(
    vararg options: OpenOption,
    sync: Boolean = true,
    f: (OutputStream) -> T
): T {
    return atomicWrite(sync) { path ->
        Files.newOutputStream(path, *options).use { output ->
            f(output)
        }
    }
}
