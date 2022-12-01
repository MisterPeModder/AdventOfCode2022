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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedInputStream
import java.io.IOException
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

@JvmRecord
@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class Configuration(
    val token: String?,
    val year: Int,
    val day: Int,
) {
    companion object {
        val DEFAULT = Configuration(token = null, year = 2022, day = 1)

        val DIRECTORY: Path = Path(".aoc")
        val CONFIG_PATH: Path = DIRECTORY.resolve("config.json")

        /**
         * Ensures that the configuration directory exists, or creates it if not.
         *
         * @throws IOException
         */
        fun makeDirectory() {
            if (!DIRECTORY.exists()) DIRECTORY.createDirectory()
            else if (!DIRECTORY.isDirectory()) throw IOException("$DIRECTORY exists, but is not a directory")
        }

        /**
         * Restores a previously-saved configuration from disk.
         * Uses default config if not present.
         */
        fun load(path: Path = CONFIG_PATH): Configuration {
            if (!path.exists()) return DEFAULT
            return try {
                BufferedInputStream(path.inputStream(StandardOpenOption.READ)).use(
                    Json::decodeFromStream
                )
            } catch (e: Exception) {
                when (e) {
                    is IOException, is SerializationException -> {
                        System.err.println("Failed to load configuration from disk ($path), using default, reason: ${e.message}")
                        DEFAULT
                    }

                    else -> throw e
                }
            }
        }
    }

    /**
     * @throws IOException
     */
    fun save(path: Path = CONFIG_PATH) {
        makeDirectory()
        path.outputStream(
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
        ).buffered().use {
            Json.encodeToStream(this, it)
        }
    }
}
