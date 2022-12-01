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

package com.misterpemodder.aoc2022

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal object SolutionTests {
    @Test
    fun day00() {
        runBlocking {
            val raw = ByteReadChannel("Sample data".toByteArray())
            val data = timed { Day00.setup(raw) }

            assertNotNull(data.result)

            val result1 = timed { Day00.part1(data.result) }
            assertEquals(11, result1.result)

            val result2 = timed { Day00.part2(data.result) }
            assertEquals(44, result2.result)
        }
    }
}

private object Day00 : Solution<Day00.Data> {
    override val year = 0
    override val day = 0

    @JvmRecord
    data class Data(val raw: String)

    override suspend fun setup(input: ByteReadChannel): Data = Data(input.readAllUTF8())

    override fun part1(data: Data): Long = data.raw.length.toLong()

    override fun part2(data: Data): Long = data.raw.length.toLong() * 4
}
