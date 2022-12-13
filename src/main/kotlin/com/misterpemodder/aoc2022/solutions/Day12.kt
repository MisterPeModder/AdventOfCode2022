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
import com.misterpemodder.aoc2022.util.Side
import com.misterpemodder.aoc2022.util.Vec2i
import io.ktor.utils.io.*
import it.unimi.dsi.fastutil.bytes.ByteArrayList

@RegisterSolution(year = 2022, day = 12)
internal class Day12 : Solution<HeightMap> {
    override suspend fun setup(input: ByteReadChannel): HeightMap {
        val data = ByteArrayList()
        var width = Int.MAX_VALUE
        var height = 0
        lateinit var start: Vec2i
        lateinit var end: Vec2i

        input.utf8Lines().collect { line ->
            width = width.coerceAtMost(line.length)
            val y = height++
            data.ensureCapacity(data.size + width)

            for ((x, c) in line.chars().iterator().withIndex()) {
                when (c) {
                    'S'.code -> {
                        start = Vec2i(x, y)
                        data += 0
                    }

                    'E'.code -> {
                        end = Vec2i(x, y)
                        data += 25
                    }

                    in 'a'.code..'z'.code -> data += (c - 'a'.code).toByte()
                    else -> throw RuntimeException("Character $c encountered not in range (a-z)")
                }
            }
        }
        return HeightMap(ByteGrid(data.elements(), width, height), start, end)
    }

    override fun part1(data: HeightMap): Long = breadthFirstSearch(grid = data.grid,
        start = data.start,
        endReached = { it == data.end },
        canReach = { pos, next -> data.grid.get(next) <= data.grid.get(pos) + 1 })

    override fun part2(data: HeightMap): Long = breadthFirstSearch(grid = data.grid,
        start = data.end,
        endReached = { data.grid.get(it).toInt() == 0 },
        canReach = { pos, next -> data.grid.get(pos) - 1 <= data.grid.get(next) })
}

internal fun breadthFirstSearch(
    grid: ByteGrid,
    start: Vec2i,
    endReached: (Vec2i) -> Boolean,
    canReach: (Vec2i, Vec2i) -> Boolean
): Long {
    val posQueue = ArrayDeque(listOf(start))
    val costsQueue = ArrayDeque(listOf(0L))
    val visited = ByteGrid.zeroed(grid.width, grid.height)

    visited.set(0, 0, 1)

    while (posQueue.isNotEmpty()) {
        val pos = posQueue.removeFirst()
        val cost = costsQueue.removeFirst()

        if (endReached(pos)) return cost

        for (side in Side.values()) {
            val nextPos = pos + side.offset

            if (!grid.isInBounds(nextPos) || visited.get(nextPos).toInt() != 0 || !canReach(
                    pos, nextPos
                )
            ) continue

            posQueue.addLast(nextPos)
            costsQueue.addLast(cost + 1)
            visited.set(nextPos, (cost + 1).toByte())
        }
    }
    return Long.MAX_VALUE
}

internal data class HeightMap(val grid: ByteGrid, val start: Vec2i, val end: Vec2i)
