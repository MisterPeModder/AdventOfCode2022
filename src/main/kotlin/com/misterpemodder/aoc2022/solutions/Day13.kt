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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@RegisterSolution(year = 2022, day = 13)
internal object Day13 : Solution<List<Value>> {
    override suspend fun setup(input: ByteReadChannel): List<Value> =
        input.utf8Lines().filter { it.isNotEmpty() }.map { parseValue(Source(it)) }.toList()

    override fun part1(data: List<Value>): Long = data.asSequence()
        .chunked(2)
        .withIndex()
        .filter { (_, pair) -> pair[0] < pair[1] }
        .map { (i, _) -> i + 1 }
        .sum()
        .toLong()

    override fun part2(data: List<Value>): Long {
        val packets = ArrayList(data)
        val divider1 = ListValue(listOf(ListValue(listOf(IntValue(2)))))
        val divider2 = ListValue(listOf(ListValue(listOf(IntValue(6)))))

        packets += divider1
        packets += divider2
        packets.sort()

        return ((packets.indexOf(divider1) + 1) * (packets.indexOf(divider2) + 1)).toLong()
    }
}

private data class Source(var src: String)

/** Quick 'n dirty parsing */
private fun parseValue(source: Source): Value {
    val digits = source.src.takeWhile { it.isDigit() }

    return if (digits.isNotEmpty()) {
        source.src = source.src.substring(digits.length)
        IntValue(digits.toInt())
    } else if (source.src.first() == '[') {
        val list = mutableListOf<Value>()
        source.src = source.src.substring(1)
        while (source.src.first() != ']') {
            list += parseValue(source)
            if (source.src.first() == ',') source.src = source.src.substring(1)
        }
        source.src = source.src.substring(1)
        ListValue(list)
    } else {
        throw RuntimeException("unexpected character in ${source.src}")
    }
}

internal sealed interface Value : Comparable<Value>

@JvmInline
private value class ListValue(val inner: List<Value>) : Value {
    override fun compareTo(other: Value): Int = when (other) {
        is IntValue -> compareTo(ListValue(listOf(other)))
        is ListValue -> inner.compareTo(other.inner)
    }

    override fun toString(): String = "[${inner.joinToString(",")}]"
}

@JvmInline
private value class IntValue(val inner: Int) : Value {
    override fun compareTo(other: Value): Int = when (other) {
        is ListValue -> ListValue(listOf(this)).compareTo(other)
        is IntValue -> inner - other.inner
    }

    override fun toString(): String = inner.toString()
}

private fun <T : Comparable<T>> List<T>.compareTo(other: List<T>): Int {
    for (i in 0 until (size.coerceAtMost(other.size))) {
        val res = this[i].compareTo(other[i])
        if (res != 0) return res
    }
    return size - other.size
}
