package com.example.androiddevchallenge.util

import java.util.concurrent.TimeUnit

fun Long.secondsToHours() = TimeUnit.SECONDS.toHours(this)
fun Long.secondsToMinutes() = TimeUnit.SECONDS.toMinutes(this)

fun Long.hoursToSeconds() = TimeUnit.HOURS.toSeconds(this)
fun Long.minutesToSeconds() = TimeUnit.MINUTES.toSeconds(this)
