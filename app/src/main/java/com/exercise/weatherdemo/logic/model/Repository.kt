package com.exercise.weatherdemo.logic.model

import androidx.lifecycle.liveData
import com.exercise.weatherdemo.common.PlaceLocation
import com.exercise.weatherdemo.logic.DAO.PlaceDao
import com.exercise.weatherdemo.logic.model.Weather
import com.exercise.weatherdemo.logic.network.WeatherDemoNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {
    val placeListLiveData = PlaceDao.placeListLiveData

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = WeatherDemoNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                WeatherDemoNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                WeatherDemoNetwork.getDailyWeather(lng, lat)
            }
            val deferredHourly = async {
                WeatherDemoNetwork.getHourlyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            val hourlyResponse = deferredHourly.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok" && hourlyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily, hourlyResponse.result.hourly)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    fun savePlace(place: ArrayList<Place>) = PlaceDao.savePlace(place)

    fun getPlace() = PlaceDao.getPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun requestLocationUpdate() = PlaceLocation.requestLocationUpdate()
    fun removeLocationRequest() = PlaceLocation.removeLocationRequest()
}