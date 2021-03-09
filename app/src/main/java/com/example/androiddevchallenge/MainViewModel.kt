package com.example.androiddevchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.util.hoursToSeconds
import com.example.androiddevchallenge.util.minutesToSeconds
import com.example.androiddevchallenge.util.secondsToHours
import com.example.androiddevchallenge.util.secondsToMinutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {

    private var countdownJob: Job? = null

    private val totalSecondsMutable = MutableStateFlow(0L)
    val totalSeconds: StateFlow<Long> = totalSecondsMutable

    private val activeMutable = MutableStateFlow(false)
    val active: StateFlow<Boolean> = activeMutable

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
        if (activeMutable.value) {
            stop()
        } else {
            start()
        }
    }

    private fun stop() {
        Timber.d("testiq: stop")
        countdownJob?.cancel()
        activeMutable.value = false 
    }

    private fun start() {
        Timber.d("testiq: start")
        countdownJob = viewModelScope.launch {
            interval().collect {
                Timber.d("testiq: tick")
                totalSecondsMutable.value--
                if (totalSecondsMutable.value == 0L) {
                    stop()
                    // TODO[kovalyov] (09-Mar-2021): Play sound
                }
            }
        }
        activeMutable.value = true
    }

    private fun interval() = flow {
        while (true) {
            delay(1000)
            emit(Unit)
        }
    }
}
