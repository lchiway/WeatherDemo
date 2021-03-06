package com.exercise.weatherdemo.common

import com.exercise.weatherdemo.logic.model.Place

/**
 *  Author : lchiway
 *  Date   : 2021/4/16
 *  Desc   :
 */

object CommonObject {
    fun checkPlaceContains(list: ArrayList<Place>, place: Place): Int{
        for(i in 0 until list.size){
            if(list[i].name == place.name)
                return i
        }
        return -1
    }
}