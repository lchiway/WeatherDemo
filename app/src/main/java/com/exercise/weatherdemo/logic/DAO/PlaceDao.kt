package com.exercise.weatherdemo.logic.DAO

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.exercise.weatherdemo.WeatherDemoApplication
import com.exercise.weatherdemo.logic.model.Place
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser


/**
 * @author lvzw
 * @date 2021年04月12日 8:56
 */
object PlaceDao {
    private val placeList = ArrayList<Place>()
    val placeListLiveData = MutableLiveData<ArrayList<Place>>()
    private fun getSharedPreferences() = WeatherDemoApplication.context.getSharedPreferences(
        DaoConstant.PLACE_SHAREDPREFERENCE,
        Context.MODE_PRIVATE
    )

    fun savePlace(place: ArrayList<Place>){
        getSharedPreferences().edit{
            putString("place", Gson().toJson(place))
        }
        placeListLiveData.value = place
    }

    fun getPlace(): ArrayList<Place> {
        try {
            val gson = Gson()
            val array: JsonArray =
                JsonParser().parse(getSharedPreferences().getString("place", "")).asJsonArray
            placeList.clear()
            for (jsonElement in array) {
                val place = gson.fromJson(jsonElement, Place::class.java)
                placeList.add(place)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return placeList
    }

    fun isPlaceSaved(): Boolean{
        return getSharedPreferences().contains("place")
    }
}