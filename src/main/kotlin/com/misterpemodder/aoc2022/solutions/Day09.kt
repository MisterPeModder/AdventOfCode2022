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
import com.misterpemodder.aoc2022.util.Side
import com.misterpemodder.aoc2022.util.Vec2i
import io.ktor.utils.io.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
@RegisterSolution(year = 2022, day = 9)
internal object Day09 : Solution<List<Side>> {
    override suspend fun setup(input: ByteReadChannel): List<Side> =
        input.utf8Lines().flatMapConcat { line ->
            val side = when (val s = line[0]) {
                'U' -> Side.TOP
                'D' -> Side.BOTTOM
                'L' -> Side.LEFT
                'R' -> Side.RIGHT
                else -> throw RuntimeException("Unknown side: $s")
            }
            val count = line.substring(2).toInt()

            (0 until count).asFlow().map { side }
        }.toList()

    override fun part1(data: List<Side>): Long = moveRope(data, 2)

    override fun part2(data: List<Side>): Long = moveRope(data, 10)

    private fun moveRope(moves: List<Side>, ropeSize: Int): Long {
        val knots = (0 until ropeSize).map { Vec2i(0, 0) }
        val visitedByTail = mutableSetOf<Vec2i>()

        for (side in moves) {
            knots[0] += side.offset

            for ((head, tail) in knots.windowed(2)) {
                val tailOffset = head - tail

                // if tail is not next to head, move it
                if (!(tailOffset.x in -1..1 && tailOffset.y in -1..1))
                    tail += tailOffset.signs
            }

            if (knots.last() !in visitedByTail)
                visitedByTail += knots.last().copy()
        }
        return visitedByTail.size.toLong()
    }
}
