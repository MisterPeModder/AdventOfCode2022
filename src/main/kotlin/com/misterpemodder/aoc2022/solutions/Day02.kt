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

@RegisterSolution(year = 2022, day = 2)
internal object Day02 : Solution<StrategyGuide> {
    override suspend fun setup(input: ByteReadChannel): StrategyGuide {
        // initialize strengths and weaknesses of each shape
        // we cannot put this inside the 'Shape' class because it would reference itself.
        val shapes = Shape.values()
        for (shape in shapes) {
            shape.strongAgainst = shapes[(shape.ordinal - 1).mod(shapes.size)]
            shape.weakAgainst = shapes[(shape.ordinal + 1).mod(shapes.size)]
        }

        val opponentShapes = mutableListOf<Shape>()
        val ownShapes = mutableListOf<Shape>()

        input.utf8Lines().collect { line ->
            // input line is two letters separated by a single space
            opponentShapes += when (val opponentShape = line[0]) {
                'A' -> Shape.ROCK
                'B' -> Shape.PAPER
                'C' -> Shape.SCISSORS
                else -> throw RuntimeException("Unexpected opponent shape: $opponentShape")
            }

            ownShapes += when (val ownShape = line[2]) {
                'X' -> Shape.ROCK
                'Y' -> Shape.PAPER
                'Z' -> Shape.SCISSORS
                else -> throw RuntimeException("Unexpected own shape: $ownShape")
            }
        }

        return StrategyGuide(opponentShapes, ownShapes)
    }

    /** Computes the sum of all round results by following the guide exactly. */
    override fun part1(data: StrategyGuide): Long = data.opponentShapes.asSequence()
        .zip(data.ownShapes.asSequence())
        .map { (opponentShape, ownShape) ->
            ownShape.score + ownShape.round(opponentShape).score
        }
        .sum()

    /** Computes the sum of all round results by treating second column of the guide as the outcome to get. */
    override fun part2(data: StrategyGuide): Long = data.opponentShapes.asSequence()
        .zip(data.ownShapes.asSequence().map(Shape::toOutcome))
        .map { (opponentShape, outcome) ->
            outcome.score + when (outcome) {
                Outcome.WIN -> opponentShape.weakAgainst.score
                Outcome.LOSS -> opponentShape.strongAgainst.score
                Outcome.DRAW -> opponentShape.score
            }
        }
        .sum()
}

/** @param score The points to award as a result of this outcome. */
enum class Outcome(val score: Long) {
    WIN(6L), LOSS(0L), DRAW(3L)
}

enum class Shape {
    ROCK, PAPER, SCISSORS;

    /** The points to award when this shape is chosen by the player. */
    val score: Long = ordinal + 1L

    /** The shape that this instance will always win against. */
    lateinit var strongAgainst: Shape

    /** The shape that this instance will always lose against. */
    lateinit var weakAgainst: Shape

    /** Plays a round of rock-paper-scissors */
    fun round(other: Shape): Outcome = if (other === this.strongAgainst) {
        Outcome.WIN
    } else if (other === this.weakAgainst) {
        Outcome.LOSS
    } else {
        Outcome.DRAW
    }

    /** @return The outcome that needs to happen if this shape is selected by the opposing player. */
    fun toOutcome(): Outcome = when (this) {
        ROCK -> Outcome.LOSS
        PAPER -> Outcome.DRAW
        SCISSORS -> Outcome.WIN
    }
}

/** The parsed input data of day 2. */
data class StrategyGuide(val opponentShapes: List<Shape>, val ownShapes: List<Shape>)
