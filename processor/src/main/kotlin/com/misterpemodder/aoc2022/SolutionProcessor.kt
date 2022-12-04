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

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

class SolutionProcessor(private val codeGenerator: CodeGenerator) :
    SymbolProcessor {
    private var processed: Boolean = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation("com.misterpemodder.aoc2022.RegisterSolution")
        val solutionClasses = symbols.filterIsInstance<KSClassDeclaration>()
            .filter(KSClassDeclaration::validate)
            .toList()

        val packageName = "com.misterpemodder.aoc2022.generated"
        val className = "Solutions"


        if (processed)
            return emptyList()
        processed = true

        val solutions = solutionClasses.map {
            val (year, day) = getAocYearAndDay(it)
            Triple(year, day, it.qualifiedName!!.asString())
        }.joinToString(separator = "\n        ") { (year, day, name) ->
            "solutions.put(Pair($year, $day), \"$name\")"
        }

        codeGenerator.createNewFile(
            Dependencies(
                aggregating = false
            ), packageName, className
        ).use { file ->
            file.write(
                """
package $packageName
        
object $className {
    private val SOLUTION_NAMES_BY_YEAR_AND_DAY: Map<Pair<Int, Int>, String>;
                    
    init {
        val solutions = mutableMapOf<Pair<Int, Int>, String>()
                        
        $solutions
    
        SOLUTION_NAMES_BY_YEAR_AND_DAY = solutions
    }
                    
    fun getName(year: Int, day: Int): String? = SOLUTION_NAMES_BY_YEAR_AND_DAY.get(Pair(year, day))
}""".trimIndent().toByteArray()
            )
        }

        return emptyList()
    }
}

private fun getAocYearAndDay(classDef: KSClassDeclaration): Pair<Int, Int> {
    val args =
        classDef.annotations.find { it.shortName.getShortName() == "RegisterSolution" }!!.arguments

    val year = args.find { it.name?.getShortName() == "year" }!!.value!! as Int
    val day = args.find { it.name?.getShortName() == "day" }!!.value!! as Int
    return Pair(year, day)
}

class SolutionProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        SolutionProcessor(environment.codeGenerator)
}

