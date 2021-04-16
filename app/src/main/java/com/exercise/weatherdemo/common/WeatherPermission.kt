package com.exercise.weatherdemo.common

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.exercise.weatherdemo.WeatherDemoApplication.Companion.context

/**
 *  Author : lchiway
 *  Date   : 2021/4/16
 *  Desc   :
 */
object WeatherPermission {
    private val permission = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun checkPermission(activity: Activity): Boolean {
        for (i in permission.indices) {
            val checkPermission = context.let{ActivityCompat.checkSelfPermission(it, permission[i])}
            if (checkPermission == PackageManager.PERMISSION_DENIED) {
                requestWeatherPermission(activity)
                return false
            }
        }
        return true
    }

    private fun requestWeatherPermission(activity: Activity){
        activity.requestPermissions(permission, 0)
    }
}