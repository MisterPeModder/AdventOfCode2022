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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@RegisterSolution(year = 2022, day = 3)
internal object Day03 : Solution<List<ByteArray>> {
    override suspend fun setup(input: ByteReadChannel) = input.utf8Lines().map { line ->
        val items = ByteArray(line.length)
        for ((i, c) in line.toCharArray().withIndex()) {
            when (c) {
                in 'a'..'z' -> items[i] = (c.code - 'a'.code + 1).toByte()
                in 'A'..'Z' -> items[i] = (c.code - 'A'.code + 27).toByte()
                else -> throw RuntimeException("Invalid character $c: expected alphabetic char")
            }
        }
        items
    }.toList()

    override fun part1(data: List<ByteArray>): Long = data.asSequence().map {
        val mid = it.size / 2
        val part1 = it.copyOfRange(0, mid)
        val part2 = it.copyOfRange(mid, it.size)

        part1.intersect(part2.toSet()).first().toLong()
    }.sum()

    override fun part2(data: List<ByteArray>): Long =
        data.asSequence().chunked(3).map { (elf1, elf2, elf3) ->
            elf1.intersect(elf2.toSet()).intersect(elf3.toSet()).first().toLong()
        }.sum()
}
