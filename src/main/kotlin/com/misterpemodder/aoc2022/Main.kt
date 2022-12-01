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

suspend fun main() {
    val config = Configuration.load()
    val token = config.token ?: throw IllegalStateException("Missing API token")

    config.save()

    val httpClient = HttpClient(CIO) {
        defaultRequest {
            url("https://adventofcode.com")
            cookie("session", token)
        }
    }

    fetchInput(httpClient, 2022, 1).utf8Lines().collect(::println)
}
