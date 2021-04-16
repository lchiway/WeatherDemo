package com.exercise.weatherdemo.ui.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.exercise.weatherdemo.common.CommonObject
import com.exercise.weatherdemo.logic.model.Location
import com.exercise.weatherdemo.logic.model.Place
import com.exercise.weatherdemo.logic.model.Repository

/**
 *  Author : lchiway
 *  Date   : 2021/4/4
 *  Desc   :
 */
class WeatherViewModel : ViewModel(){
    private val locationLiveData = MutableLiveData<Location>()
    var locationLng = ""
    var locationLat = ""
    var placeName = ""
    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String){
        locationLiveData.value = Location(lng, lat)
    }

    fun savePlace() {
        placeName += "(定位）"
        val place: Place = Place(placeName, Location(locationLng, locationLat), "")
        val placeList = Repository.getPlace()
        if(placeList.size==0) {
            placeList.add(place)
            Repository.savePlace(placeList)
        }
        else {
            placeList[0] = place
            Repository.savePlace(placeList)
        }
    }
}