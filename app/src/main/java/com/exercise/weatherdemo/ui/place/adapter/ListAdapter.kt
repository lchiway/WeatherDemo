package com.exercise.weatherdemo.ui.place.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exercise.weatherdemo.R
import com.exercise.weatherdemo.common.SwipeLinearLayout
import com.exercise.weatherdemo.logic.model.data.PlaceListItem
import com.exercise.weatherdemo.logic.model.getSky
import com.exercise.weatherdemo.ui.place.fragment.ListFragment
import com.exercise.weatherdemo.ui.weather.activity.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.list_place_item.*


/**
 * @author lvzw
 * @date 2021年04月14日 9:58
 */
class ListAdapter(
    private val fragment: ListFragment,
    private val placeItemList: ArrayList<PlaceListItem>
): RecyclerView.Adapter<ListAdapter.ViewHolder>(), SwipeLinearLayout.OnSwipeListener {

    private val swipeLinearLayouts = ArrayList<SwipeLinearLayout>()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.listPlaceName)
        val placeTemp: TextView = view.findViewById(R.id.listPlaceTemp)
        val listPlaceLayout: LinearLayout = view.findViewById(R.id.listPlaceLayout)
        val swipeLinearLayout: SwipeLinearLayout = view.findViewById(R.id.swipeListLayout)
        val deleteListItem: FrameLayout = view.findViewById(R.id.deleteListItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_place_item,
            parent,
            false
        )
        val holder = ViewHolder(view)
        holder.swipeLinearLayout.setSwipeListener(this)
        holder.listPlaceLayout.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeItemList[position].place
            val activity = fragment.activity
            if (activity is WeatherActivity) {
                activity.drawerLayout.closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather(placeItemList[position].weather)
            }
            //holder.swipeLinearLayout.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK)
        }
        holder.deleteListItem.setOnClickListener {
            Log.d("lvzw", "onCreateViewHolder: ${holder.adapterPosition}, ${placeItemList.size}")
            if(holder.adapterPosition != 0) {
                fragment.viewModel.deleteListItem(
                    placeItemList[holder.adapterPosition].place,
                    holder.adapterPosition
                )
                holder.swipeLinearLayout.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK)
            }
        }
        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var temp = 0.0F
        holder.placeName.text = placeItemList[position].place.name
        if(placeItemList.size > 0 && placeItemList.size > position) {
            temp = placeItemList[position].weather.realtime.temperature
            holder.listPlaceLayout.setBackgroundResource(getSky(placeItemList[position].weather.realtime.skycon).bg)
        }
        holder.placeTemp.text = "$temp ℃"
        swipeLinearLayouts.add(holder.swipeLinearLayout)
    }

    override fun onDirectionJudged(sll: SwipeLinearLayout?, isHorizontal: Boolean) {
        if (isHorizontal) {
            for (swipeLinearLayout in swipeLinearLayouts) {
                swipeLinearLayout.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK)
            }
        } else {
            for (swipeLinearLayout in swipeLinearLayouts) {
                if (swipeLinearLayout != sll) {
                    swipeLinearLayout.scrollAuto(SwipeLinearLayout.DIRECTION_SHRINK)
                }
            }
        }
    }


    override fun getItemCount() = placeItemList.size
}