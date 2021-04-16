package com.exercise.weatherdemo.common

import android.location.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.exercise.weatherdemo.WeatherDemoApplication.Companion.context
import com.exercise.weatherdemo.logic.model.data.CurrentLocation
import java.io.IOException
import java.util.*

/**
 *  Author : lchiway
 *  Date   : 2021/4/16
 *  Desc   :
 */
object PlaceLocation {
    private var locationManager : LocationManager? = null
    private var updatePeriod: Long = 5*60*1000
    var currentLocation: CurrentLocation = CurrentLocation("", "", "")
    var curLocLiveData = MutableLiveData<CurrentLocation>()

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //Log.d("lvzw", "onLocationChanged: get the locations: ${location.toString()}")
            currentLocation.lng = location.longitude.toString()
            currentLocation.lat = location.latitude.toString()
            try {
                val geoCoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address> = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude, 1
                )
                val sb = StringBuilder()
                if (addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    sb.append(address.locality).append("\n")
                    currentLocation.name = sb.toString()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            curLocLiveData.value = currentLocation
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun requestLocationUpdate(){
        locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        try {
            // Request location updates
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                updatePeriod,
                0f,
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * @param period: update period
     * @Description: For update period settings
     */
    fun setUpdatePeriod(period: Long){
        updatePeriod = period
    }

    fun removeLocationRequest(){
        locationManager?.removeUpdates(locationListener)
    }
}