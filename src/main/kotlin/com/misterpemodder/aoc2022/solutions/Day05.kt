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
import it.unimi.dsi.fastutil.bytes.ByteArrayList

@RegisterSolution(year = 2022, day = 5)
internal object Day05 : Solution<Data> {
    private val ACTION_PATTERN: Regex = """^move (\d+) from (\d+) to (\d+)$""".toRegex()

    override suspend fun setup(input: ByteReadChannel): Data {
        val stackLines = mutableListOf<List<Char>>()
        val actions = mutableListOf<Action>()

        input.utf8Lines().collect {
            if (it.contains('[')) {
                val stackLine = mutableListOf<Char>()
                var column = 1

                while (column < it.length) {
                    stackLine += it[column]
                    column += 4
                }
                stackLines += stackLine
            } else {
                val match = ACTION_PATTERN.matchEntire(it)

                if (match !== null) {
                    val (amount, from, to) = match.destructured

                    actions += Action(from.toInt(), to.toInt(), amount.toInt())
                }
            }
        }
        val stacks: Array<ByteArrayList> =
            List(stackLines.last().size) { ByteArrayList() }.toTypedArray()

        for (stackLine in stackLines.asReversed()) {
            for (i in stacks.indices) {
                if (i < stackLine.size && !stackLine[i].isWhitespace()) {
                    stacks[i].push(stackLine[i].code.toByte())
                }
            }
        }
        return Data(stacks, actions)
    }

    override fun part1(data: Data): Long {
        val stacks = data.stacks.map(::ByteArrayList).toTypedArray()
        val actions = data.actions

        for ((from, to, amount) in actions) {
            repeat(amount) {
                stacks[to - 1].push(stacks[from - 1].popByte())
            }
        }
        print("Top of the stacks: ")
        println(String(stacks.map { it.topByte().toInt().toChar() }.toCharArray()))
        return -1 // this is one of the rare exercises that does not return a number
    }

    override fun part2(data: Data): Long {
        val (stacks, actions) = data

        for ((from, to, amount) in actions) {
            val fromStack = stacks[from - 1]
            val toStack = stacks[to - 1]
            val fromSize = fromStack.size

            toStack.addAll(fromStack.subList(fromSize - amount, fromSize))
            fromStack.removeElements(fromSize - amount, fromSize)
        }
        print("Top of the stacks: ")
        println(String(stacks.map { it.topByte().toInt().toChar() }.toCharArray()))
        return -1 // this is one of the rare exercises that does not return a number
    }
}

@Suppress("ArrayInDataClass")
internal data class Data(val stacks: Array<ByteArrayList>, val actions: List<Action>)

internal data class Action(val from: Int, val to: Int, val amount: Int)
