package com.exercise.weatherdemo.common

/**
 * @author lvzw
 * @date 2021年04月15日 15:51
 */
class UpdateEvent() {
    // object{
    var updateEvent: Boolean = true
    //}

    init {
        updateEvent = false
    }

    constructor(update: Boolean): this(){
        updateEvent = update
    }
}