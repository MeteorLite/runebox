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

package io.runebox.asm

object ClassVersionUtils {
    private fun swapWords(v: Int): Int {
        return (v shl 16) or (v ushr 16)
    }

    fun gte(v1: Int, v2: Int): Boolean {
        return swapWords(v1) >= swapWords(v2)
    }

    fun max(v1: Int, v2: Int): Int {
        return if (gte(v1, v2)) {
            v1
        } else {
            v2
        }
    }
}
