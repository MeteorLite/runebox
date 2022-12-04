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

/**
 * A data structure containing a set of elements partitioned into a number of
 * non-overlapping subsets. New elements belong to singleton subsets. The
 * [union] function combines two subsets together into a single larger subset.
 */
interface DisjointSet<T> : Iterable<DisjointSet.Partition<T>> {
    interface Partition<T> : Iterable<T>

    val elements: Int
    val partitions: Int

    fun add(x: T): Partition<T>
    operator fun get(x: T): Partition<T>?
    fun union(x: Partition<T>, y: Partition<T>)
}
