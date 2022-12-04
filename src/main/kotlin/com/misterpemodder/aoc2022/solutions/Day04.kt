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

@RegisterSolution(year = 2022, day = 4)
object Day04 : Solution<List<Pair<IntRange, IntRange>>> {
    private val LINE_PATTERN: Regex = """^(\d+)-(\d+),(\d+)-(\d+)$""".toRegex()

    override suspend fun setup(input: ByteReadChannel): List<Pair<IntRange, IntRange>> =
        input.utf8Lines().map { line ->
            val (b1, e1, b2, e2) = LINE_PATTERN.matchEntire(line)!!.destructured
            Pair(b1.toInt()..e1.toInt(), b2.toInt()..e2.toInt())
        }.toList()

    override fun part1(data: List<Pair<IntRange, IntRange>>): Long =
        data.count { (elf1, elf2) -> elf1.containsAll(elf2) || elf2.containsAll(elf1) }.toLong()

    override fun part2(data: List<Pair<IntRange, IntRange>>): Long =
        data.count { (elf1, elf2) -> elf1.overlapsWith(elf2) }.toLong()
}

private fun IntRange.containsAll(range: IntRange): Boolean =
    contains(range.first) && contains(range.last)

private fun IntRange.overlapsWith(range: IntRange): Boolean =
    contains(range.first) || contains(range.last) || range.contains(first)
