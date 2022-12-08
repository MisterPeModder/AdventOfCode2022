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
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongComparators
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap

@RegisterSolution(year = 2022, day = 7)
internal object Day07 : Solution<List<Command>> {
    private const val SMALL_DIR_THRESHOLD: Long = 100000L
    private const val TOTAL_DISK_SPACE: Long = 70000000L
    private const val MINIMUM_UNUSED_SPACE: Long = 30000000L

    override suspend fun setup(input: ByteReadChannel): List<Command> {
        val commands = mutableListOf<Command>()
        var lastEntries: MutableList<Entry>? = null

        input.utf8Lines().collect { line ->
            if (line.startsWith("$ ")) {
                val args = line.substring(2).split(' ')

                commands += when (val commandName = args.getOrElse(0) { "" }) {
                    "cd" -> CdCommand(args[1])
                    "ls" -> {
                        val ls = LsCommand(mutableListOf())
                        lastEntries = ls.entries
                        ls
                    }

                    "" -> return@collect // break
                    else -> throw RuntimeException("Unsupported command $commandName")
                }
            } else if (lastEntries !== null) {
                lastEntries!!.add(Entry.of(line))
            }
        }
        return commands
    }

    override fun part1(data: List<Command>): Long {
        val fs = makeFileSystem(data)
        val totalSize = Measurement(0L)

        fs.measureSizes(totalSize, SMALL_DIR_THRESHOLD)
        return totalSize.value
    }

    override fun part2(data: List<Command>): Long {
        val fs = makeFileSystem(data)
        val dirSizesWithNames = Object2LongOpenHashMap<String>()
        val usedSpace = fs.collectSizes(dirSizesWithNames)
        val freeSpace = TOTAL_DISK_SPACE - usedSpace
        val spaceToReclaim = (MINIMUM_UNUSED_SPACE - freeSpace).coerceAtLeast(0L)

        var smallestReclaim = Long.MAX_VALUE

        val dirSizes = LongArrayList(dirSizesWithNames.values)

        dirSizes.sort(LongComparators.OPPOSITE_COMPARATOR)

        for (size in dirSizes.longIterator()) {
            if (size in spaceToReclaim until smallestReclaim) smallestReclaim = size
        }
        return smallestReclaim
    }
}

internal sealed interface Command

internal data class CdCommand(val path: String) : Command

internal data class LsCommand(val entries: MutableList<Entry>) : Command

internal sealed interface Entry {
    val name: String

    companion object {
        fun of(line: String): Entry {
            val separator = line.indexOf(' ')

            if (separator == -1) throw IllegalArgumentException("ls entry must contain at least two words")
            val sizeOrDir = line.substring(0, separator)
            val name = line.substring(separator + 1)

            if (sizeOrDir == "dir") return DirectoryEntry(name, mutableMapOf())
            return FileEntry(name, sizeOrDir.toLong())
        }
    }
}


internal data class DirectoryEntry(
    override val name: String, val children: MutableMap<String, Entry>
) : Entry {
    var parent: DirectoryEntry?
        get() = children[".."] as DirectoryEntry?
        set(value) {
            when (value) {
                null -> children.remove("..")
                else -> children[".."] = value
            }
        }

    private val path: String
        get() = when (val parent = this.parent) {
            null -> "/"
            else -> "${parent.path}$name/"
        }

    override fun toString(): String = path

    fun measureSizes(totalSize: Measurement, threshold: Long): Long {
        var ownSize = 0L

        for ((name, child) in children) {
            if (name != "..") ownSize += when (child) {
                is FileEntry -> child.size
                is DirectoryEntry -> child.measureSizes(totalSize, threshold)
            }
        }

        if (ownSize <= threshold) totalSize.value += ownSize

        return ownSize
    }

    fun collectSizes(dirSizes: Object2LongMap<String>): Long {
        var ownSize = 0L

        for ((name, child) in children) {
            if (name == "..") continue
            ownSize += when (child) {
                is FileEntry -> child.size
                is DirectoryEntry -> child.collectSizes(dirSizes)
            }
        }

        dirSizes[path] = ownSize
        return ownSize
    }
}

internal data class Measurement(var value: Long)

internal data class FileEntry(override val name: String, val size: Long) : Entry

internal fun makeFileSystem(commands: List<Command>): DirectoryEntry {
    val root = DirectoryEntry("", mutableMapOf())
    var cwd = root

    for (command in commands) {
        when (command) {
            is CdCommand -> cwd = resolvePath(command.path, root, cwd)

            is LsCommand -> for (entry in command.entries) {
                if (entry is DirectoryEntry) {
                    entry.parent = cwd
                }
                cwd.children[entry.name] = entry
            }
        }
    }

    return root
}

internal fun resolvePath(path: String, root: DirectoryEntry, cwd: DirectoryEntry): DirectoryEntry {
    val (startPath, startDir) = if (path.startsWith('/')) {
        Pair(path.substring(1), root)
    } else {
        Pair(path, cwd)
    }
    return startPath.splitToSequence('/').fold(startDir) { dir, part ->
        if (part.isEmpty()) {
            dir
        } else {
            val nextResult =
                dir.children[part] ?: throw RuntimeException("no such directory: $part in $dir")

            if (nextResult !is DirectoryEntry) throw throw RuntimeException("not a directory")
            nextResult
        }
    }
}
