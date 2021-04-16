package com.exercise.weatherdemo.ui.place.viewmodel

import androidx.lifecycle.ViewModel
import com.exercise.weatherdemo.logic.model.Repository
import com.exercise.weatherdemo.logic.model.Weather
import com.exercise.weatherdemo.logic.model.data.PlaceListItem

/**
 * @author lvzw
 * @date 2021年04月14日 11:29
 */
class ListPlaceViewModel: ViewModel() {
    var placeListLiveData = Repository.placeListLiveData
    val placeList = getPlace()
    val placeItemList = ArrayList<PlaceListItem>()
    fun getPlace() = Repository.getPlace()
}