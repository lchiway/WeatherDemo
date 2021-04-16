package com.exercise.weatherdemo.ui.place.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.exercise.weatherdemo.logic.model.Place
import com.exercise.weatherdemo.logic.model.Repository

class PlaceViewModel: ViewModel() {
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData){query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String){
        searchLiveData.value = query
    }

    fun getPlace() = Repository.getPlace()

    fun savePlace(placeList: ArrayList<Place>) = Repository.savePlace(placeList)
}