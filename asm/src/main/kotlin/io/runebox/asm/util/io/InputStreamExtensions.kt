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

import java.io.InputStream
import java.util.Arrays

fun InputStream.contentEquals(other: InputStream): Boolean {
    val buf1 = ByteArray(4096)
    val buf2 = ByteArray(4096)

    while (true) {
        val n1 = read(buf1, 0, buf1.size)
        if (n1 == -1) {
            return other.read() == -1
        }

        var off = 0
        var remaining = n1
        while (remaining > 0) {
            val n2 = other.read(buf2, off, remaining)
            if (n2 == -1) {
                return false
            }

            off += n2
            remaining -= n2
        }

        @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
        if (!Arrays.equals(buf1, 0, n1, buf2, 0, n1)) {
            return false
        }
    }
}
