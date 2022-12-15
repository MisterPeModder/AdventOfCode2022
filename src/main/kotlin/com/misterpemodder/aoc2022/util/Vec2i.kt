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

package com.misterpemodder.aoc2022.util

import kotlin.math.abs
import kotlin.math.sign

data class Vec2i(var x: Int, var y: Int) {
    val signs: Vec2i
        get() = Vec2i(x.sign, y.sign)

    operator fun plus(other: Vec2i): Vec2i = Vec2i(x + other.x, y + other.y)

    operator fun plusAssign(other: Vec2i) {
        x += other.x
        y += other.y
    }

    operator fun minus(other: Vec2i) = Vec2i(x - other.x, y - other.y)

    operator fun minusAssign(other: Vec2i) {
        x -= other.x
        y -= other.y
    }

    override fun toString(): String = "($x, $y)"

    fun copyFrom(source: Vec2i) {
        this.x = source.x
        this.y = source.y
    }

    fun min(other: Vec2i): Vec2i = Vec2i(x.coerceAtMost(other.x), y.coerceAtMost(other.y))
    fun max(other: Vec2i): Vec2i = Vec2i(x.coerceAtLeast(other.x), y.coerceAtLeast(other.y))

    fun manhattanDistance(other: Vec2i): Int = manhattanDistance(other.x, other.y)
    fun manhattanDistance(x: Int, y: Int): Int = abs(this.x - x) + abs(this.y - y)
}
