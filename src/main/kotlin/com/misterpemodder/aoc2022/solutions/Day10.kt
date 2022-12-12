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

@RegisterSolution(year = 2022, day = 10)
internal object Day10 : Solution<List<Instruction>> {
    override suspend fun setup(input: ByteReadChannel): List<Instruction> =
        input.utf8Lines().map { line ->
            val params = line.splitToSequence(' ').iterator()
            when (val mnemonic = params.next()) {
                "addx" -> AddX(params.next().toInt())
                "noop" -> Noop
                else -> throw RuntimeException("unknown mnemonic: $mnemonic")
            }
        }.toList()

    override fun part1(data: List<Instruction>): Long {
        var x = 1L
        var strengths = 0L
        var targetCycle = 20
        var cycleDelay = 1
        val instructions = data.iterator()

        for (cycle in 2..220) {
            if (cycle == targetCycle) {
                strengths += cycle * x
                targetCycle += 40
            }
            if (cycleDelay <= 1) {
                val instruction = instructions.next()

                cycleDelay = instruction.cycles
                if (instruction is AddX) x += instruction.param
            } else {
                --cycleDelay
            }
        }
        return strengths
    }

    override fun part2(data: List<Instruction>): Long {
        var cycle = 0
        var x = 1
        val instructions = data.iterator()
        var currentInstruction = instructions.next()
        var cycleDelay = currentInstruction.cycles

        while (instructions.hasNext()) {
            val row = cycle++ % 40

            if (row in (x - 1..x + 1))
                print('#')
            else
                print('.')
            if (row == 39) println()

            if (cycleDelay > 1) {
                --cycleDelay
            } else {
                if (currentInstruction is AddX) x += currentInstruction.param

                if (instructions.hasNext()) {
                    currentInstruction = instructions.next()
                    cycleDelay = currentInstruction.cycles
                }
            }
        }
        println()
        return -1L
    }
}

internal interface Instruction {
    val cycles: Int
}

internal object Noop : Instruction {
    override val cycles: Int = 1
}

internal data class AddX(val param: Int) : Instruction {
    override val cycles: Int = 2
}
