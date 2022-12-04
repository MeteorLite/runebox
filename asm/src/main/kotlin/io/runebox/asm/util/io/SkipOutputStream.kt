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

class SkipOutputStream(private val out: OutputStream, private var skipBytes: Long) : OutputStream() {
    override fun write(b: Int) {
        if (skipBytes == 0L) {
            out.write(b)
        } else {
            skipBytes--
        }
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (len > skipBytes) {
            out.write(b, off + skipBytes.toInt(), len - skipBytes.toInt())
            skipBytes = 0
        } else {
            skipBytes -= len
        }
    }

    override fun flush() {
        out.flush()
    }

    override fun close() {
        out.close()
    }
}
