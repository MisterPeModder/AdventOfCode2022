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

import com.misterpemodder.aoc2022.Solution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal object Day00 : Solution<Day00.Data> {
    @JvmRecord
    data class Data(val raw: String)

    override val name: String
        get() = "Sample AOC solution"

    override suspend fun setup(input: Flow<Char>): Data =
        Data(input.fold(StringBuilder(), StringBuilder::append).toString())

    override fun part1(data: Data): Long = data.raw.length.toLong()

    override fun part2(data: Data): Long = data.raw.length.toLong() * 4

    @Test
    fun day00Test() {
        val data = runBlocking { setup("Sample data".asSequence().asFlow()) }
        assertEquals(11, part1(data))
        assertEquals(44, part2(data))
    }
}

