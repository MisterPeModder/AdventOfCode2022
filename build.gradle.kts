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

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    application
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.7.21"))
    }
}

group = "com.misterpemodder"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinVersion: String by project
val kotlinxCoroutinesVersion: String by project
val kotlinxSerializationVersion: String by project
val ktorVersion: String by project
val fastUtilVersion: String by project
val multikVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("it.unimi.dsi:fastutil:$fastUtilVersion")

    implementation("org.jetbrains.kotlinx:multik-core:$multikVersion")
    implementation("org.jetbrains.kotlinx:multik-openblas:$multikVersion")

    testImplementation(kotlin("test"))

    implementation(project(":processor"))
    ksp(project(":processor"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("com.misterpemodder.aoc2022.MainKt")
}

tasks.withType<Jar> {
    archivesName.set("aoc2022")
    manifest {
        attributes["Main-Class"] = "com.misterpemodder.aoc2022.MainKt"
    }
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}
