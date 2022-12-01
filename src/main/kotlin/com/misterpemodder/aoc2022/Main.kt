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

import kotlin.system.exitProcess

suspend fun main(args: Array<String>) {
    val config = parseArguments(Configuration.load(), args)

    println(args.joinToString())
    config.save()

    fetchInput(config).utf8Lines().collect(::println)
}

private fun parseArguments(baseConfig: Configuration, args: Array<String>): Configuration {
    var token = baseConfig.token
    var year = baseConfig.year
    var day = baseConfig.day

    val iterator = args.iterator()

    while (iterator.hasNext()) {
        when (val arg = iterator.next().trim()) {
            "-h", "--help" -> showHelp()
            "-y", "--year" -> year = iterator.nextValue(arg, String::toInt)
            "-d", "--day" -> day = iterator.nextValue(arg, String::toInt)
            "-t", "--token" -> token = iterator.nextValue(arg) { this }

            else -> throw RuntimeException("Unknown argument: $arg")
        }
    }
    return Configuration(token = token, year = year, day = day)
}

private fun <T> Iterator<String>.nextValue(name: String, parser: String.() -> T): T {
    try {
        return next().trim().let(parser)
    } catch (ignored: RuntimeException) {
        throw RuntimeException("Missing or invalid value to argument $name")
    }
}

private fun showHelp(): Nothing {
    println(
        """
        Usage: aoc2022 [-h] [--year YEAR] [--day DAY] [--token TOKEN]
        
        Advent of Code runner
        Configuration and cache files are located in the .aoc directory at the current working directory.
        
        Options:
          -h, --help  show this help message and exit
          -y, --year  the aoc year
          -d, --day   the challenge day number, ranging from 1 to 25 (inclusive)
          -t, --token a request token for downloading challenge input over HTTP
    """.trimIndent()
    )
    exitProcess(0)
}

