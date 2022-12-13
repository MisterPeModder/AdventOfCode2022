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
import com.misterpemodder.aoc2022.util.iteratorCollect
import com.misterpemodder.aoc2022.util.nextOrThrow
import com.misterpemodder.aoc2022.util.product
import io.ktor.utils.io.*
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList
import kotlinx.coroutines.channels.ChannelIterator

@RegisterSolution(year = 2022, day = 11)
internal object Day11 : Solution<Array<Monkey>> {
    override suspend fun setup(input: ByteReadChannel): Array<Monkey> =
        input.utf8Lines().iteratorCollect {
            val monkeys = mutableListOf<Monkey>()

            while (it.hasNext()) monkeys += Monkey.parse(it)
            monkeys.toTypedArray()
        }

    override fun part1(data: Array<Monkey>): Long {
        val monkeys = data.map(Monkey::deepCopy).toTypedArray()
        repeat(20) {
            monkeyRound(monkeys) { it / 3L }
        }
        return monkeys.map(Monkey::inspectedItems).sortedDescending().take(2).product()
    }

    override fun part2(data: Array<Monkey>): Long {
        val monkeys = data.map(Monkey::deepCopy).toTypedArray()
        val max = monkeys.asSequence().map(Monkey::divisibilityTest).product()
        repeat(10000) {
            monkeyRound(monkeys) { it % max }
        }
        return monkeys.map(Monkey::inspectedItems).sortedDescending().take(2).product()
    }
}


private fun monkeyRound(monkeys: Array<Monkey>, worryReduce: (Long) -> Long) {
    for (monkey in monkeys) {
        for (item in monkey.items.longIterator()) {
            ++monkey.inspectedItems
            val worryLevel = worryReduce(monkey.operation(item))
            val target = if (worryLevel % monkey.divisibilityTest == 0L) {
                monkey.targetOnSuccess
            } else {
                monkey.targetOnFailure
            }
            monkeys[target].items.add(worryLevel)
        }
        monkey.items.clear()
    }
}

internal data class Monkey(
    val items: LongList,
    val operation: Operation,
    val divisibilityTest: Long,
    val targetOnSuccess: Int,
    val targetOnFailure: Int,
    var inspectedItems: Long
) {
    companion object {
        internal suspend fun parse(it: ChannelIterator<String>): Monkey {
            var line = it.nextOrThrow()
            while (!line.startsWith("Monkey")) {
                line = it.nextOrThrow()
            }

            val items = it.nextOrThrow()
                .removePrefix("  Starting items: ")
                .splitToSequence(", ")
                .fold(LongArrayList()) { items, item ->
                    items.add(item.toLong())
                    items
                }
            val operationStr = it.nextOrThrow().removePrefix("  Operation: new = ")

            return Monkey(
                items = items,
                operation = if (operationStr == "old * old") {
                    SquareOperation
                } else if (operationStr.startsWith("old + ")) {
                    AddOperation(operationStr.substring(6).toLong())
                } else if (operationStr.startsWith("old * ")) {
                    MultiplyOperation(operationStr.substring(6).toLong())
                } else {
                    throw RuntimeException("unknown operation: $operationStr")
                },
                divisibilityTest = it.nextOrThrow().removePrefix("  Test: divisible by ").toLong(),
                targetOnSuccess = it.nextOrThrow()
                    .removePrefix("    If true: throw to monkey ")
                    .toInt(),
                targetOnFailure = it.nextOrThrow()
                    .removePrefix("    If false: throw to monkey ")
                    .toInt(),
                inspectedItems = 0L
            )
        }
    }

    fun deepCopy(): Monkey = copy(items = LongArrayList(items))
}

internal interface Operation {
    operator fun invoke(old: Long): Long
}

internal data class AddOperation(val amount: Long) : Operation {
    override fun invoke(old: Long): Long = old + amount
}

internal data class MultiplyOperation(val amount: Long) : Operation {
    override fun invoke(old: Long): Long = old * amount
}

internal object SquareOperation : Operation {
    override fun invoke(old: Long): Long = old * old
}
