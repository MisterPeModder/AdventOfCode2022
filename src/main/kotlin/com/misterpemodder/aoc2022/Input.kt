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

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException
import java.nio.file.StandardOpenOption
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

/**
 * Fetch the input of a specific AOC day and year.
 * Tries to read a cache located in .aoc/YEAR/DAY.txt before attempting to download via HTTP.
 */
internal suspend fun fetchInput(config: Configuration): ByteReadChannel {
    val cacheDirectory = Configuration.DIRECTORY.resolve(config.year.toString())

    if (!cacheDirectory.exists()) cacheDirectory.createDirectories()
    val cachePath = cacheDirectory.resolve("day%02d.txt".format(config.day))

    if (!cachePath.exists()) {
        val url = "/${config.year}/day/${config.day}/input"
        val response = aocHttpClient(config).get(url)
        if (!response.status.isSuccess()) throw RuntimeException("Failed to fetch input from $url: HTTP response ${response.status}")

        cachePath.outputStream(
            StandardOpenOption.WRITE, StandardOpenOption.CREATE
        ).use {
            response.bodyAsChannel().toInputStream().copyTo(it)
        }
    }

    return cachePath.inputStream(StandardOpenOption.READ).buffered().toByteReadChannel()
}

private fun aocHttpClient(config: Configuration): HttpClient {
    val token = config.token ?: throw IllegalStateException("Missing API token")

    return HttpClient(CIO) {
        defaultRequest {
            url("https://adventofcode.com")
            cookie("session", token)
        }
    }
}

fun ByteReadChannel.utf8Lines(): Flow<String> = flow {
    val buffer = StringBuilder()

    while (readUTF8LineTo(buffer, Int.MAX_VALUE)) {
        emit(buffer.toString())
        buffer.clear()
    }
}

suspend fun ByteReadChannel.readAllUTF8(): String {
    val buffer = StringBuilder()

    while (readUTF8LineTo(buffer, Int.MAX_VALUE)) buffer.append('\n')
    if (buffer.isNotEmpty()) buffer.deleteCharAt(buffer.length - 1)
    return buffer.toString()
}
