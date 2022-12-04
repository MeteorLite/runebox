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

import java.io.OutputStream
import java.nio.file.attribute.FileTime
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.Deflater
import java.util.zip.ZipEntry

class DeterministicJarOutputStream : JarOutputStream {
    constructor(out: OutputStream) : super(out)
    constructor(out: OutputStream, man: Manifest) : super(out, man)

    init {
        setLevel(Deflater.BEST_COMPRESSION)
    }

    override fun putNextEntry(ze: ZipEntry) {
        ze.creationTime = UNIX_EPOCH
        ze.lastAccessTime = UNIX_EPOCH
        ze.lastModifiedTime = UNIX_EPOCH
        super.putNextEntry(ze)
    }

    private companion object {
        private val UNIX_EPOCH = FileTime.fromMillis(0)
    }
}
