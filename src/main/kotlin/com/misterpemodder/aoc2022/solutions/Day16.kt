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
import io.ktor.utils.io.*
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set

@RegisterSolution(year = 2022, day = 16)
internal object Day16 : Solution<List<Day16.Valve>> {
    @JvmInline
    internal value class ValveId(private val raw: Int) {
        companion object {
            fun fromString(str: String): ValveId =
                ValveId((str[0].code and 0xff) or ((str[1].code and 0xff) shl 8))
        }

        operator fun component1(): Char = (raw and 0xff).toChar()
        operator fun component2(): Char = ((raw shr 8) and 0xff).toChar()

        override fun toString(): String = "${component1()}${component2()}"
    }

    data class Valve(
        val id: ValveId,
        val flowRate: Int,
        val neighbors: Array<Valve>,
    ) {
        override fun toString(): String = "Valve $id has flow rate=$flowRate;"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Valve

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int = id.hashCode()
    }

    private val PATTERN: Regex =
        """^Valve (..) has flow rate=(\d+); tunnels? leads? to valves? (.*)$""".toRegex()

    override suspend fun setup(input: ByteReadChannel): List<Valve> {
        val valveTunnels = input.utf8Lines().map { line ->
            val (rawId, rawFlowRate, rawTunnels) = PATTERN.matchEntire(line)!!.destructured
            val id = ValveId.fromString(rawId)
            val neighbors = rawTunnels.splitToSequence(", ").map { ValveId.fromString(it) }.toList()
            @Suppress("UNCHECKED_CAST")
            Pair(
                Valve(
                    id,
                    rawFlowRate.toInt(),
                    // temporary uninitialized array
                    arrayOfNulls<Valve?>(neighbors.size) as Array<Valve>,
                ), neighbors
            )
        }.toList()

        for ((valve, tunnels) in valveTunnels) for (i in tunnels.indices) valve.neighbors[i] =
            valveTunnels.first { it.first.id == tunnels[i] }.first
        return valveTunnels.map { it.first }.sortedByDescending(Valve::flowRate)
    }

    override fun part1(data: List<Valve>): Long {
        val valvePositions = data.withIndex().fold(Object2IntOpenHashMap<Valve>()) { map, valve ->
            map[valve.value] = valve.index
            map
        }

        val workingValves = data.filter { it.flowRate > 0 }
        val valveCount = data.size
        val workingValveCount = workingValves.size
        val valvesCombinations = 1 shl workingValveCount

        val cache = mk.zeros<Int>(30, valveCount, valvesCombinations)
        val flowRates = data.map(Valve::flowRate)

        for (timeLeft in 1 until 30) { // for every minute
            println("timeLeft: $timeLeft")
            for ((pos, valve) in data.withIndex()) {
                val ii = 1 shl pos
                for (openValves in 0 until valvesCombinations) {
                    var cached = cache[timeLeft, pos, openValves]
                    if ((ii and openValves != 0) && timeLeft >= 2) {
                        val prev = cache[timeLeft - 1, pos, openValves - ii]
                        cached = cached.coerceAtLeast(prev + flowRates[pos] * timeLeft)
                    }
                    for (neighbor in valve.neighbors) {
                        val neighborPos = valvePositions.getInt(neighbor)
                        cached =
                            cached.coerceAtLeast(cache[timeLeft - 1, neighborPos, openValves])
                    }
                    cache[timeLeft, pos, openValves] = cached
                }
            }
        }

        val valveAAId = ValveId.fromString("AA")
        val valveAAPos = data.withIndex().first { it.value.id == valveAAId }.index
        return cache[29, valveAAPos, valvesCombinations - 1].toLong()
    }

    override fun part2(data: List<Valve>): Long {
        TODO("Not yet implemented")
    }
}

