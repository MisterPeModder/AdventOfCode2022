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

import com.misterpemodder.aoc2022.Configuration.Companion.DEFAULT
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

internal object ConfigurationTests {
    @Test
    fun defaultConfig() {
        assertEquals(null, DEFAULT.token)
        assertEquals(2022, DEFAULT.year)
        assertEquals(1, DEFAULT.day)
    }

    @Test
    fun loadNonExisting() {
        val path = File.createTempFile("aoc2022_", "_empty").toPath()
        assertEquals(DEFAULT, Configuration.load(path))
    }

    @Test
    fun saveAndLoad() {
        val path = File.createTempFile("aoc2022_", "_save_and_load").toPath()
        val source = Configuration(token = "1234-secret~token", year = 2042, day = 2)

        source.save(path)
        val loaded = Configuration.load(path)

        assertEquals(source, loaded)
    }
}
