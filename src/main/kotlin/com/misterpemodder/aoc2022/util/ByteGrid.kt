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

internal data class ByteGrid(val data: ByteArray, val width: Int, val height: Int) {
    companion object {
        fun zeroed(width: Int, height: Int): ByteGrid =
            ByteGrid(ByteArray(width * height) { 0 }, width, height)
    }

    fun get(pos: Vec2i): Byte = get(pos.x, pos.y)
    fun get(x: Int, y: Int): Byte = data[y * width + x]

    fun set(pos: Vec2i, value: Byte) = set(pos.x, pos.y, value)
    fun set(x: Int, y: Int, value: Byte) {
        data[y * width + x] = value
    }

    fun isInBounds(pos: Vec2i): Boolean = isInBounds(pos.x, pos.y)
    fun isInBounds(x: Int, y: Int): Boolean = x in 0 until width && y in 0 until height

    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteGrid

        return data.contentEquals(other.data) && width == other.width && height == other.height
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    fun deepCopy(): ByteGrid = ByteGrid(data.copyOf(), width, height)
}
