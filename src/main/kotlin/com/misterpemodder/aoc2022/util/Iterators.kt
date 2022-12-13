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

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.runBlocking

/**
 * Makes the data inside this [Flow] accessible through a channel.
 */
@OptIn(FlowPreview::class)
internal fun <T, R> Flow<T>.iteratorCollect(scope: suspend (ChannelIterator<T>) -> R): R =
    runBlocking {
        val channel = this@iteratorCollect.produceIn(this)
        scope(channel.iterator())
    }

/**
 * Get the next item from the channel, or throw if no items remain.
 */
internal suspend fun <E> ChannelIterator<E>.nextOrThrow(): E = if (hasNext()) {
    next()
} else {
    throw NoSuchElementException()
}

/**
 * Computes the product of all elements in this iterable object.
 */
internal fun Iterable<Long>.product(): Long = fold(1L) { p, x -> p * x }

/**
 * Computes the product of all elements in this sequence.
 */
internal fun Sequence<Long>.product(): Long = fold(1L) { p, x -> p * x }
