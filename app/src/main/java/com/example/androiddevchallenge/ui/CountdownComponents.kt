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
package com.example.androiddevchallenge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.model.CountdownState
import com.example.androiddevchallenge.ui.theme.MyTheme

private const val MAX_HOURS = 99
private const val MAX_MINUTES = 59
private const val MAX_SECONDS = 59

@Composable
fun CountDownInputRow(
    active: Boolean,
    totalSeconds: Long,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onSecondsChange: (Int) -> Unit,
) {
    val (hours, minutes, seconds) = totalSeconds.secondsToHms()
    val keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = hours.formatHms(),
            onValueChange = { hoursStr ->
                var newHours = hoursStr.toIntOrNull() ?: 0
                when {
                    newHours < 0 -> newHours = 0
                    newHours > MAX_HOURS -> newHours = MAX_HOURS
                }
                onHoursChange(newHours)
            },
            modifier = Modifier.width(72.dp),
            textStyle = TextStyle(fontSize = 32.sp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            enabled = !active,
        )
        Text(
            text = ":",
            modifier = Modifier.padding(16.dp),
            style = TextStyle(fontSize = 32.sp),
        )
        TextField(
            value = minutes.formatHms(),
            onValueChange = {
                var newMinutes = it.toIntOrNull() ?: 0
                when {
                    newMinutes < 0 -> newMinutes = 0
                    newMinutes > MAX_MINUTES -> newMinutes = MAX_MINUTES
                }
                onMinutesChange(newMinutes)
            },
            modifier = Modifier.width(72.dp),
            textStyle = TextStyle(fontSize = 32.sp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            enabled = !active,
        )
        Text(
            text = ":",
            modifier = Modifier.padding(16.dp),
            style = TextStyle(fontSize = 32.sp),
        )
        TextField(
            value = seconds.formatHms(),
            onValueChange = {
                var newSeconds = it.toIntOrNull() ?: 0
                when {
                    newSeconds < 0 -> newSeconds = 0
                    newSeconds > MAX_SECONDS -> newSeconds = MAX_SECONDS
                }
                onSecondsChange(newSeconds)
            },
            modifier = Modifier.width(72.dp),
            textStyle = TextStyle(fontSize = 32.sp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            enabled = !active,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CountDownActionsRow(
    enabled: Boolean,
    state: CountdownState,
    startStopAction: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier.padding(16.dp),
    ) {
        Button(
            onClick = {
                keyboardController?.hideSoftwareKeyboard()
                startStopAction()
            },
            enabled = enabled,
        ) {
            Text(
                text = when (state) {
                    CountdownState.IDLE -> "Start"
                    CountdownState.TICKING -> "Cancel"
                    CountdownState.ALARM -> "Turn Off"
                }
            )
        }
    }
}

private fun Long.secondsToHms(): Triple<Long, Long, Long> {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return Triple(hours, minutes, seconds)
}

private fun Long.formatHms() = String.format("%02d", this)

@Preview
@Composable
fun CountDownInputRowPreview() {
    MyTheme {
        CountDownInputRow(
            active = true,
            totalSeconds = 10 * 60 + 30,
            onHoursChange = {},
            onMinutesChange = {},
            onSecondsChange = {}
        )
    }
}

@Preview
@Composable
fun CountDownActionsRowPreview() {
    MyTheme {
        CountDownActionsRow(enabled = true, state = CountdownState.ALARM, {})
    }
}
