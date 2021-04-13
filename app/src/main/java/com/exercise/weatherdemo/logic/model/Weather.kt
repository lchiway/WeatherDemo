package com.exercise.weatherdemo.logic.model

data class Weather(val realtime: RealtiimResponse.Realtime, val daily: dailyResponse.Daily, val hourly: HourlyResponse.Hourly)