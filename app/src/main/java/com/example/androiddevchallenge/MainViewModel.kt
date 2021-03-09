/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.model.CountdownState
import com.example.androiddevchallenge.util.hoursToSeconds
import com.example.androiddevchallenge.util.minutesToSeconds
import com.example.androiddevchallenge.util.secondsToHours
import com.example.androiddevchallenge.util.secondsToMinutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var countdownJob: Job? = null

    private val totalSecondsMutable = MutableStateFlow(0L)
    val totalSeconds: StateFlow<Long> = totalSecondsMutable

    private val stateMutable = MutableStateFlow(CountdownState.IDLE)
    val state: StateFlow<CountdownState> = stateMutable

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventFlow = eventChannel.receiveAsFlow()

    fun hoursChanged(value: Int) {
        val newHours = value.toLong()
        val oldHours = totalSeconds.value.secondsToHours()
        val newTotal = totalSeconds.value - oldHours.hoursToSeconds() + newHours.hoursToSeconds()
        totalSecondsMutable.value = newTotal
    }

    fun minutesChanged(value: Int) {
        val newMinutes = value.toLong()
        val oldMinutes = totalSeconds.value.secondsToMinutes()
        val newTotal = totalSeconds.value - oldMinutes.minutesToSeconds() + newMinutes.minutesToSeconds()
        totalSecondsMutable.value = newTotal
    }

    fun secondsChanged(value: Int) {
        val oldSeconds = totalSeconds.value % 60
        val newTotal = totalSeconds.value - oldSeconds + value
        totalSecondsMutable.value = newTotal
    }

    fun startOrStop() {
        when (state.value) {
            CountdownState.IDLE -> start()
            CountdownState.TICKING -> stop()
            CountdownState.ALARM -> {
                eventChannel.offer(Event.StopSound)
                stateMutable.value = CountdownState.IDLE
            }
        }
    }

    fun stopAlarm() {
        stateMutable.value = CountdownState.IDLE
    }

    private fun stop() {
        countdownJob?.cancel()
        stateMutable.value = CountdownState.IDLE
    }

    private fun start() {
        countdownJob = viewModelScope.launch {
            interval().collect {
                totalSecondsMutable.value--
                if (totalSecondsMutable.value == 0L) {
                    countdownJob?.cancel()
                    stateMutable.value = CountdownState.ALARM
                    eventChannel.send(Event.PlaySound)
                }
            }
        }
        stateMutable.value = CountdownState.TICKING
    }

    private fun interval() = flow {
        while (true) {
            delay(1000)
            emit(Unit)
        }
    }

    sealed class Event {
        object PlaySound : Event()
        object StopSound : Event()
    }
}
