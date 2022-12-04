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

import com.misterpemodder.aoc2022.generated.Solutions
import io.ktor.utils.io.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

internal interface Solution<Data> {
    suspend fun setup(input: ByteReadChannel): Data

    fun part1(data: Data): Long

    fun part2(data: Data): Long
}

/**
 * Get the instance of [Solution] corresponding  to the given year and day.
 */
internal fun Solutions.get(year: Int, day: Int): Solution<*>? {
    val name = getName(year, day) ?: return null

    @Suppress("UNCHECKED_CAST")
    val clazz: KClass<Solution<*>> = Class.forName(name).kotlin as KClass<Solution<*>>

    if (clazz.objectInstance !== null)
        return clazz.objectInstance
    return clazz.createInstance()
}

/**
 * Executes this solution, printing the results to stdout.
 */
internal suspend fun <Data> Solution<Data>.run(year: Int, day: Int, input: ByteReadChannel) {
    println("Solving day $day of year $year...")

    val timedData = timed { setup(input) }
    println("Processed data in ${timedData.elapsedMillis}ms")

    val data = timedData.result
    if (data === null) {
        if (timedData.error !== null) println("error while processing data, ${timedData.error}")
        else println("Missing data, cannot execute solution parts")
        return
    }

    val result1 = timed { part1(data) }
    println("Part 1: $result1")

    val result2 = timed { part2(data) }
    println("Part 2: $result2")

    val elapsed = timedData.elapsedMillis + result1.elapsedMillis + result2.elapsedMillis
    println("Total elapsed time: %.3fms".format(elapsed))
}

/**
 * Runs a (suspend) function with a timer
 */
internal suspend fun <T> timed(fn: suspend () -> T): TimedResult<T> {
    val start = System.nanoTime()
    return try {
        TimedResult(System.nanoTime() - start, result = fn())
    } catch (e: Throwable) {
        when (e) {
            is Exception -> TimedResult(System.nanoTime() - start, error = e)
            is NotImplementedError -> TimedResult(0)
            else -> throw e
        }
    }
}

/**
 * Holds the result of a timed execution
 * @see timed
 */
internal data class TimedResult<T>(
    val elapsedNanos: Long, val error: Exception? = null, val result: T? = null
) {
    val elapsedMillis: Double
        get() = elapsedNanos.toDouble() / 1000

    override fun toString(): String {
        return if (error !== null) {
            "failed in %.3fms: $error".format(elapsedMillis)
        } else if (result === null) {
            "not implemented"
        } else {
            "%s (in %.3fms)".format(result, elapsedMillis)
        }
    }
}
