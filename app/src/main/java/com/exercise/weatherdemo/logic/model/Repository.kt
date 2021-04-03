package com.exercise.weatherdemo.logic.model

import androidx.lifecycle.liveData
import com.exercise.weatherdemo.logic.network.WeatherDemoNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

object Repository {
    fun searchPlaces(query: String) = liveData(Dispatchers.IO){
        val result = try{
            val placeResponse = WeatherDemoNetwork.searchPlaces(query)
            if(placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)
            }
            else{
                Result.failure(RuntimeException ("response status is ${placeResponse.status}"))
            }
        }
        catch (e: Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}