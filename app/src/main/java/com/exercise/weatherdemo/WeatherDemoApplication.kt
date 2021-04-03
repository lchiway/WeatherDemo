package com.exercise.weatherdemo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class WeatherDemoApplication : Application(){
    companion object{
        const val TOKEN = "gZI9DDoweEH75Gdt"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
