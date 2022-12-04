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
import com.misterpemodder.aoc2022.utf8Lines
import com.misterpemodder.aoc2022.RegisterSolution
import io.ktor.utils.io.*
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongComparators
import it.unimi.dsi.fastutil.longs.LongList
import kotlinx.coroutines.flow.fold

@RegisterSolution(year = 2022, day = 1)
internal object Day01 : Solution<LongList> {
    override suspend fun setup(input: ByteReadChannel): LongList {
        var elfCalories = 0L
        val allCalories = input.utf8Lines().fold(LongArrayList()) { caloriesGroups, line ->
            if (line.isNotEmpty()) {
                elfCalories += line.toLong()
            } else {
                caloriesGroups += elfCalories
                elfCalories = 0
            }
            caloriesGroups
        }

        allCalories.sort(LongComparators.OPPOSITE_COMPARATOR)
        return allCalories
    }

    override fun part1(data: LongList): Long = data.getLong(0)

    override fun part2(data: LongList): Long = data.getLong(0) + data.getLong(1) + data.getLong(2)
}
