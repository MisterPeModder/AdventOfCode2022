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
import com.misterpemodder.aoc2022.util.toInt
import com.misterpemodder.aoc2022.utf8Lines
import com.misterpemodder.aoc2022.util.Side
import io.ktor.utils.io.*
import it.unimi.dsi.fastutil.bytes.ByteArrayList

@RegisterSolution(year = 2022, day = 8)
internal object Day08 : Solution<TreeGrid> {
    override suspend fun setup(input: ByteReadChannel): TreeGrid {
        val data = ByteArrayList()
        var width = Int.MAX_VALUE
        var height = 0

        input.utf8Lines().collect { line ->
            width = width.coerceAtMost(line.length)
            ++height
            data.ensureCapacity(data.size + width)
            for (c in line.chars()) {
                if (c !in '0'.code..'9'.code) throw RuntimeException("Non digit character $c encountered")
                data += (c - '0'.code).toByte()
            }
        }

        return TreeGrid(data.elements(), width, height)
    }

    override fun part1(data: TreeGrid): Long = data.foldLong { visibleTrees, x, y ->
        visibleTrees + data.isVisible(x, y).toInt()
    }

    override fun part2(data: TreeGrid): Long = data.foldLong { highestScore, x, y ->
        highestScore.coerceAtLeast(data.scenicScore(x, y).toLong())
    }
}

@Suppress("ArrayInDataClass")
internal data class TreeGrid(val data: ByteArray, val width: Int, val height: Int) {
    fun get(x: Int, y: Int): Int = data[y * width + x].toInt()

    fun isVisible(x: Int, y: Int): Boolean = isVisibleFrom(x, y, Side.TOP) ||
            isVisibleFrom(x, y, Side.BOTTOM) ||
            isVisibleFrom(x, y, Side.LEFT) ||
            isVisibleFrom(x, y, Side.RIGHT)

    fun scenicScore(x: Int, y: Int): Int = countVisibleTowards(x, y, Side.TOP) *
            countVisibleTowards(x, y, Side.BOTTOM) *
            countVisibleTowards(x, y, Side.LEFT) *
            countVisibleTowards(x, y, Side.RIGHT)

    fun foldLong(initial: Long = 0L, operation: (Long, Int, Int) -> Long): Long {
        var acc = initial

        for (x in 0 until width) {
            for (y in 0 until height) {
                acc = operation(acc, x, y)
            }
        }
        return acc
    }

    private fun isInBounds(x: Int, y: Int): Boolean = x in 0 until width && y in 0 until height

    private fun isVisibleFrom(x: Int, y: Int, side: Side): Boolean {
        if (!isInBounds(x, y)) return false

        var (currX, currY) = when (side) {
            Side.TOP -> Pair(x, 0)
            Side.BOTTOM -> Pair(x, height - 1)
            Side.LEFT -> Pair(0, y)
            Side.RIGHT -> Pair(width - 1, y)
        }
        val treeHeight = get(x, y)

        while (!(currX == x && currY == y)) {
            if (get(currX, currY) >= treeHeight) return false
            currX -= side.xOffset
            currY -= side.yOffset
        }
        return true
    }

    private fun countVisibleTowards(x: Int, y: Int, side: Side): Int {
        if (!isInBounds(x, y)) return 0

        val treeHeight = get(x, y)
        val xOffset = side.xOffset
        val yOffset = side.yOffset
        var count = 0
        var currX = x
        var currY = y

        do {
            ++count
            currX += xOffset
            currY += yOffset
        } while (isInBounds(currX + xOffset, currY + yOffset) && get(currX, currY) < treeHeight)
        return count
    }
}
