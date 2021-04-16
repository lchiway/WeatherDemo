package com.exercise.weatherdemo.ui.place.viewmodel

import androidx.lifecycle.ViewModel
import com.exercise.weatherdemo.common.PlaceLocation.curLocLiveData
import com.exercise.weatherdemo.logic.model.Repository

/**
 * @author lvzw
 * @date 2021年04月12日 16:59
 */
class PlaceLocationViewModel: ViewModel() {
    var placeLocationLiveData = curLocLiveData

    fun requestLocationUpdate() = Repository.requestLocationUpdate()
    fun removeLocationRequest() = Repository.removeLocationRequest()
}