package com.exercise.weatherdemo.ui.weather.activity

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.exercise.weatherdemo.R
import com.exercise.weatherdemo.common.UpdateEvent
import com.exercise.weatherdemo.common.WeatherPermission
import com.exercise.weatherdemo.logic.model.Weather
import com.exercise.weatherdemo.logic.model.getSky
import com.exercise.weatherdemo.ui.place.viewmodel.PlaceLocationViewModel
import com.exercise.weatherdemo.ui.weather.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.hourly.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*


class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    private val locationViewModel by lazy { ViewModelProvider(this).get(PlaceLocationViewModel::class.java) }
    private var exitTime: Long = 0
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_weather)
        //initLocationInfo()
        scrollView = findViewById(R.id.weatherLayout)
        if(WeatherPermission.checkPermission(this)){
            locationViewModel.requestLocationUpdate()
            initObserver()
            initListener()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationViewModel.removeLocationRequest()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // location permission
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            locationViewModel.requestLocationUpdate()
            initObserver()
            initListener()
            return
        }
        finish()
    }

    private fun setFullScreen(){
        if (Build.VERSION.SDK_INT >= 30) {
            window.insetsController.also {
                it?.hide(WindowInsets.Type.statusBars())
                it?.hide(WindowInsets.Type.navigationBars())
                it?.hide(WindowInsets.Type.systemBars())
            }
        }
        else {
            val decorView = window.decorView
            window.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    private fun initLocationInfo(){
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
    }

    private fun initObserver(){
        locationViewModel.placeLocationLiveData.observe(this, Observer { result ->
            if (result.name != "") {
                viewModel.locationLng = result.lng
                viewModel.locationLat = result.lat
                viewModel.placeName = result.name
                viewModel.savePlace() //should be optimized
                refreshWeather()
            } else {
                Toast.makeText(this, "无法成功获取定位信息", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
    }

    private fun initListener(){
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        listBtn.setOnClickListener {
            EventBus.getDefault().post(UpdateEvent(true))
            drawerLayout.openDrawer(GravityCompat.END)
        }
        scrollView.viewTreeObserver.addOnScrollChangedListener( ViewTreeObserver.OnScrollChangedListener {
            swipeRefresh.isEnabled = (scrollView.scrollY == 0)
        })
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        })
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawers()
            return
        }
        if(System.currentTimeMillis() - exitTime > 2000){
            Toast.makeText(this, "Press back key twice to exit the app", Toast.LENGTH_LONG).show()
            exitTime = System.currentTimeMillis()
            return
        }
        finish()
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    fun refreshWeather(weather: Weather){
        showWeatherInfo(weather)
    }

    private fun showWeatherInfo(weather: Weather?) {
        placeName.text = viewModel.placeName
        val realtime = weather!!.realtime
        val daily = weather.daily
        val hourly = weather.hourly
        // 填充now.xml布局中数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentAQIText = "aqi: ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentAQIText
        drawerLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充hourly.xml布局中的数据
        hourly_layout.removeAllViews()
        val hours = hourly.skycon.size
        for(i in 0 until hours) {
            val skycon = hourly.skycon[i]
            val temp = hourly.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.hourly_item, hourly_layout, false)
            val hour = view.findViewById(R.id.hourly_time) as TextView
            val skyIcon = view.findViewById(R.id.hourly_skycon) as ImageView
            val temperature = view.findViewById(R.id.hourly_temp) as TextView
            hour.text = SimpleDateFormat("HH", Locale.getDefault()).format(skycon.datetime)
            skyIcon.setImageResource(getSky(skycon.value).icon)
            temperature.text = "${temp.value} ℃"
            hourly_layout.addView(view)
        }
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                forecastLayout,
                false
            )
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}