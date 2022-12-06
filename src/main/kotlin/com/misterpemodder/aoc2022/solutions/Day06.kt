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
import io.ktor.utils.io.*
import kotlin.streams.asSequence

@RegisterSolution(year = 2022, day = 6)
internal object Day06 : Solution<String> {
    override suspend fun setup(input: ByteReadChannel): String = input.readUTF8Line() ?: ""

    override fun part1(data: String): Long = firstDistinctCharSequence(data, 4)

    override fun part2(data: String): Long = firstDistinctCharSequence(data, 14)

    private fun firstDistinctCharSequence(data: String, size: Int): Long = data.chars()
        .asSequence()
        .windowed(size)
        .withIndex()
        .first { (_, values) -> values.distinct().size == values.size }.index.toLong() + size
}
