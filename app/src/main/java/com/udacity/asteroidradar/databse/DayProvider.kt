package com.udacity.asteroidradar.databse

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*

object DayProvider {
    /**
     * Get Today in Format yyyy-MM-dd
     */
    fun getToday(): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    /**
     * Get Seven Days from Now in Format yyyy-MM-dd
     */
    fun getSevenDaysLater(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}