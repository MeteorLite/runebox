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

package io.runebox.asm.util.collect

class UniqueQueue<T> {
    private val queue = ArrayDeque<T>()
    private val set = mutableSetOf<T>()

    fun add(v: T): Boolean {
        if (set.add(v)) {
            queue.addLast(v)
            return true
        }

        return false
    }

    operator fun plusAssign(v: T) {
        add(v)
    }

    fun addAll(vs: Iterable<T>) {
        for (v in vs) {
            add(v)
        }
    }

    operator fun plusAssign(vs: Iterable<T>) {
        addAll(vs)
    }

    fun removeFirstOrNull(): T? {
        val v = queue.removeFirstOrNull()
        if (v != null) {
            set.remove(v)
            return v
        }

        return null
    }

    fun clear() {
        queue.clear()
        set.clear()
    }
}
