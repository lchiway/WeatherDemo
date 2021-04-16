package com.exercise.weatherdemo.logic.model.data

import com.exercise.weatherdemo.logic.model.Place
import com.exercise.weatherdemo.logic.model.Weather
import com.exercise.weatherdemo.ui.weather.viewmodel.WeatherViewModel

/**
 *  Author : lchiway
 *  Date   : 2021/4/16
 *  Desc   :
 */
data class PlaceListItem(var place: Place, var weather: Weather, var weatherViewModel: WeatherViewModel)
