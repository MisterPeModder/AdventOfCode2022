/*
 * Copyright 2022 Yanis Guaye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.misterpemodder.aoc2022.solutions

import com.misterpemodder.aoc2022.RegisterSolution
import com.misterpemodder.aoc2022.Solution
import com.misterpemodder.aoc2022.utf8Lines
import com.misterpemodder.aoc2022.util.ByteGrid
import com.misterpemodder.aoc2022.util.Vec2i
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@RegisterSolution(year = 2022, day = 14)
internal object Day14 : Solution<Day14.Data> {
    private const val AIR: Byte = 0
    private const val STONE: Byte = 1
    private const val SAND: Byte = 2

    data class Data(val grid: ByteGrid, val min: Vec2i, val max: Vec2i, val source: Vec2i)

    override suspend fun setup(input: ByteReadChannel): Data {
        val paths: List<List<Vec2i>> = input.utf8Lines().map { line ->
            val path = mutableListOf<Vec2i>()
            var src = line

            while (src.isNotEmpty()) {
                val comma = src.indexOf(',')
                var end = src.indexOf(" -> ")
                if (end < 0) end = src.length
                path += Vec2i(
                    src.substring(0, comma).toInt(), src.substring(comma + 1, end).toInt()
                )
                src = src.substring(end).removePrefix(" -> ")
            }
            path
        }.toList()

        return drawPathsToGrid(paths)
    }

    private fun drawPathsToGrid(paths: List<List<Vec2i>>): Data {
        val source = Vec2i(500, 0)
        val min = Vec2i(
            paths.collect2d(Vec2i::x, Sequence<Int>::min).coerceAtMost(source.x),
            paths.collect2d(Vec2i::y, Sequence<Int>::min).coerceAtMost(source.y),
        )
        val max = Vec2i(
            paths.collect2d(Vec2i::x, Sequence<Int>::max).coerceAtLeast(source.x),
            paths.collect2d(Vec2i::y, Sequence<Int>::max).coerceAtLeast(source.y)
        )
        // make the grid wide enough
        min.x -= 1000
        max.x += 1000
        val size = max - min + Vec2i(1, 1)
        val grid = ByteGrid.zeroed(size.x, size.y + 2)

        for (path in paths) {
            for (pos in path) pos -= min // adjust positions to be relative to `min`
            for ((start, end) in path.windowed(2)) {
                val s = start.min(end)
                val e = start.max(end)
                when {
                    start.y == end.y -> for (x in s.x..e.x) grid.set(x, start.y, STONE)
                    start.x == end.x -> for (y in s.y..e.y) grid.set(start.x, y, STONE)
                    else -> throw RuntimeException("Cannot draw diagonal path from $start to $end")
                }
            }
        }
        return Data(grid, min, max, source - min)
    }

    private fun tryMoveSandUnit(pos: Vec2i, canMoveTo: (Int, Int) -> Boolean): Boolean {
        when {
            canMoveTo(pos.x, pos.y + 1) -> {}
            canMoveTo(pos.x - 1, pos.y + 1) -> --pos.x
            canMoveTo(pos.x + 1, pos.y + 1) -> ++pos.x
            else -> return false
        }
        ++pos.y
        return true
    }

    override fun part1(data: Data): Long {
        val grid = data.grid.deepCopy()
        val source = data.source
        var unitsRested = 0L

        while (grid.get(source) != SAND) {
            val pos = source.copy()

            while (grid.isInBounds(pos)) {
                val moved =
                    tryMoveSandUnit(pos) { x, y -> !grid.isInBounds(x, y) || grid.get(x, y) == AIR }
                if (!moved) break
            }
            if (!grid.isInBounds(pos)) break
            grid.set(pos, SAND)
            ++unitsRested
        }
        return unitsRested
    }

    override fun part2(data: Data): Long {
        val grid = data.grid
        val source = data.source
        var unitsRested = 0L

        // set the floor
        for (x in 0 until grid.width) grid.set(x, grid.height - 1, STONE)
        while (grid.get(source) != SAND) {
            val pos = source.copy()
            var moved = true

            while (moved) moved = tryMoveSandUnit(pos) { x, y -> grid.get(x, y) == AIR }

            grid.set(pos, SAND)
            ++unitsRested
        }
        return unitsRested
    }
}

private inline fun <T, R> List<List<T>>.collect2d(
    noinline mapper: (T) -> R, crossinline collector: (Sequence<R>) -> R
): R = collector(asSequence().map { collector(it.asSequence().map(mapper)) })

