package com.exercise.weatherdemo.logic.model

import java.util.*

/**
 *  Author : lchiway
 *  Date   : 2021/4/12
 *  Desc   :
 */
data class HourlyResponse(val status: String, val result: Result){
    data class Result(val hourly: Hourly)
    data class Hourly(val skycon: List<Skycon>, val temperature: List<Temperature>)
    data class Skycon(val value: String, val datetime: Date)
    data class Temperature(val value: String, val datetime: Date)
}
