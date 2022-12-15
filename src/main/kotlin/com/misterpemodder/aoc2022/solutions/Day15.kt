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
import com.misterpemodder.aoc2022.util.Vec2i
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@RegisterSolution(year = 2022, day = 15)
internal object Day15 : Solution<List<Day15.SensorResult>> {
    private val PATTERN: Regex =
        """^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$""".toRegex()

    data class SensorResult(val sensor: Vec2i, val beacon: Vec2i, val distance: Int)

    override suspend fun setup(input: ByteReadChannel): List<SensorResult> =
        input.utf8Lines().map { line ->
            val (sx, sy, bx, by) = PATTERN.matchEntire(line)!!.destructured
            val sensor = Vec2i(sx.toInt(), sy.toInt())
            val beacon = Vec2i(bx.toInt(), by.toInt())

            SensorResult(sensor, beacon, sensor.manhattanDistance(beacon))
        }.toList()

    override fun part1(data: List<SensorResult>): Long {
        val targetY = 2000000

        val grid: MutableMap<Vec2i, Char> = mutableMapOf()

        for ((sensor, beacon, dist) in data) {
            grid[beacon] = 'B'

            // exclude sensors than cannot possibly reach target row
            if (sensor.y + dist < targetY || sensor.y - dist > targetY) continue

            for (x in sensor.x - dist..sensor.x + dist) {
                val pos = Vec2i(x, targetY)
                if (!grid.containsKey(pos) && sensor.manhattanDistance(pos) <= dist) grid[pos] = '#'
            }
        }

        return grid.asSequence()
            .filter { (pos, cell) -> pos.y == targetY && cell == '#' }
            .count()
            .toLong()
    }

    override fun part2(data: List<SensorResult>): Long {
        /**
         * Checks if the given position collides with any sensor diamond.
         * Also returns true if the position is out of bounds.
         */
        fun collides(x: Int, y: Int): Boolean =
            x !in 0 until 4000000 || y !in 0 until 4000000 || data.any {
                it.sensor.manhattanDistance(x, y) <= it.distance
            }

        /** Diamond slope function. */
        fun slope(a: Int, size: Int): Int = if (a <= size) {
            a
        } else {
            -a + size + size
        }

        // Iterate over each point at the outer edge of each diamond and check if it intersects with a zone
        for ((sensor, _, dist) in data) {
            val startY = sensor.y - dist - 1

            for (offset in 0..dist * 2 + 2) {
                val y = startY + offset
                val x1 = sensor.x - slope(offset, dist + 1)
                val x2 = sensor.x + slope(offset, dist + 1)

                if (!collides(x1, y)) return x1.toLong() * 4000000L + y.toLong()
                if (!collides(x2, y)) return x2.toLong() * 4000000L + y.toLong()
            }
        }

        return -1L
    }
}

