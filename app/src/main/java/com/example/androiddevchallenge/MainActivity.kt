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

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.lifecycleScope
import com.example.androiddevchallenge.model.CountdownState
import com.example.androiddevchallenge.ui.CountDownActionsRow
import com.example.androiddevchallenge.ui.CountDownInputRow
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var alarmPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(viewModel)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.eventFlow.collect {
                handleEvent(it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (alarmPlayer?.isPlaying == true) {
            alarmPlayer?.stop()
        }
        viewModel.stopAlarm()
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmPlayer?.release()
    }

    private fun playSound() {
        alarmPlayer = MediaPlayer.create(applicationContext, getAlarmUri())
        alarmPlayer?.start()
    }

    private fun getAlarmUri() = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    private fun handleEvent(event: MainViewModel.Event) {
        when (event) {
            MainViewModel.Event.PlaySound -> playSound()
            MainViewModel.Event.StopSound -> alarmPlayer?.stop()
        }
    }
}

@Composable
fun MyApp(viewModel: MainViewModel) {
    Surface(color = MaterialTheme.colors.background) {
        val totalSeconds by viewModel.totalSeconds.collectAsState()
        val countdownState by viewModel.state.collectAsState()
        val canStart = totalSeconds != 0L || countdownState == CountdownState.ALARM
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CountDownInputRow(
                active = countdownState != CountdownState.IDLE,
                totalSeconds = totalSeconds,
                onHoursChange = viewModel::hoursChanged,
                onMinutesChange = viewModel::minutesChanged,
                onSecondsChange = viewModel::secondsChanged
            )
            CountDownActionsRow(
                enabled = canStart,
                state = countdownState,
                startStopAction = viewModel::startOrStop
            )
        }
    }
}
