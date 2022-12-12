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

/**
 * @param xOffset Moves a given horizontal coordinate closer to this side when offset
 * @param xOffset Moves a given vertical coordinate closer to this side when offset
 */
internal enum class Side(val xOffset: Int, val yOffset: Int) {
    TOP(0, -1), BOTTOM(0, 1), LEFT(-1, 0), RIGHT(1, 0);

    val offset: Vec2i = Vec2i(xOffset, yOffset)
}
