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

package io.runebox.asm.filter

object Glob {
    fun compile(pattern: String): Regex {
        return compile(pattern, className = false)
    }

    fun compileClass(pattern: String): Regex {
        return compile(pattern, className = true)
    }

    private fun compile(pattern: String, className: Boolean): Regex {
        val regex = StringBuilder()
        var star = false

        for (ch in pattern) {
            if (star) {
                star = false

                if (ch == '*') {
                    regex.append(".*")
                    continue
                }

                /*
                 * The deobfuscator uses ! in class names to separate the
                 * library name from the rest of the package/class name.
                 */
                regex.append("[^/!]*")
            }

            when (ch) {
                '*' -> if (className) {
                    star = true
                } else {
                    regex.append(".*")
                }

                else -> regex.append(Regex.escape(ch.toString()))
            }
        }

        if (star) {
            regex.append(".*")
        }

        return Regex(regex.toString())
    }
}
