package com.exercise.weatherdemo.ui.place.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.exercise.weatherdemo.R
import com.exercise.weatherdemo.common.CommonObject
import com.exercise.weatherdemo.common.UpdateEvent
import com.exercise.weatherdemo.logic.model.Place
import com.exercise.weatherdemo.logic.model.data.PlaceListItem
import com.exercise.weatherdemo.ui.place.adapter.ListAdapter
import com.exercise.weatherdemo.ui.place.viewmodel.ListPlaceViewModel
import com.exercise.weatherdemo.ui.weather.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * @author lvzw
 * @date 2021年04月13日 17:24
 */
class ListFragment: Fragment() {

    private lateinit var adapter: ListAdapter
    val viewModel by lazy { ViewModelProvider(this).get(ListPlaceViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("lvzw", "onCreateView: ")
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("lvzw", "onActivityCreated: ")
        val layoutManager = LinearLayoutManager(activity)
        listView.layoutManager = layoutManager
        initWeatherObserver(viewModel.placeList)
        adapter = ListAdapter(this, viewModel.placeItemList)
        listView.adapter = adapter
        viewModel.placeListLiveData.observe(this.viewLifecycleOwner, Observer { result ->
            if (result.size != 0) {
                Log.d("lvzw", "placeListLiveData: ${result[result.size-1]}")
                addWeatherObserver(result[result.size-1])
            }
        })
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun refreshListWeather(update: UpdateEvent){
        Log.d("lvzw", "refreshListWeather: ${update.updateEvent}")
        if(update.updateEvent) {
            for (i in 0 until viewModel.placeItemList.size) {
                viewModel.placeItemList[i].weatherViewModel.refreshWeather(
                    viewModel.placeItemList[i].place.location.lng,
                    viewModel.placeItemList[i].place.location.lat
                )
            }
        }
    }

    private fun initWeatherObserver(placeList: ArrayList<Place>){
        for(i in 0 until placeList.size){
            addWeatherObserver(placeList[i])
        }
    }

    private fun addWeatherObserver(place: Place){
        val weatherViewModel = WeatherViewModel()
        val placeList = viewModel.getPlace()
        weatherViewModel.locationLat = place.location.lat
        weatherViewModel.locationLng = place.location.lng
        weatherViewModel.placeName = place.name
        weatherViewModel.weatherLiveData.observe(this.viewLifecycleOwner, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                if(viewModel.placeItemList.size!=placeList.size || CommonObject.checkPlaceContains(placeList, place) == -1)
                    viewModel.placeItemList.add(PlaceListItem(place, weather, weatherViewModel))
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this.context, "${place.name}无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        weatherViewModel.refreshWeather(weatherViewModel.locationLng, weatherViewModel.locationLat)
    }
}