package com.exercise.weatherdemo.logic.network

import com.exercise.weatherdemo.WeatherDemoApplication
import com.exercise.weatherdemo.logic.model.RealtiimResponse
import com.exercise.weatherdemo.logic.model.dailyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/${WeatherDemoApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng")lng: String, @Path("lat")lat: String) : Call<RealtiimResponse>

    @GET("v2.5/${WeatherDemoApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng")lng: String, @Path("lat")lat: String) : Call<dailyResponse>
}