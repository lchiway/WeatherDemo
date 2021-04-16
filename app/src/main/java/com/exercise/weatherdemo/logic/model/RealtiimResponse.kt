package com.exercise.weatherdemo.logic.model

import com.google.gson.annotations.SerializedName

data class RealtiimResponse(val status: String, val result: Result){
    data class Result(val realtime: Realtime)
    data class Realtime(val skycon: String, val temperature: Float, @SerializedName("air_quality") val airQuality: AirQualty)
    data class AirQualty(val aqi: Aqi)
    data class Aqi(val chn: Float)
}