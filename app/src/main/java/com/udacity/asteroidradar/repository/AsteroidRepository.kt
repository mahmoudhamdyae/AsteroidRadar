package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.google.gson.JsonParser
import com.udacity.asteroidradar.api.Api
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.databse.AsteroidDatabase
import com.udacity.asteroidradar.databse.DayProvider
import com.udacity.asteroidradar.domain.Asteroid
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getAsteroids()

    suspend fun refreshAsteroids() {
        val response = Api.retrofitService.getAsteroids(
            DayProvider.getToday(),
            DayProvider.getSevenDaysLater()
        )
        val gson = JsonParser().parse(response.toString()).asJsonObject

        val jo2 = JSONObject(gson.toString())
        val asteroids = parseAsteroidsJsonResult(jo2)

        val dao = database.asteroidDao()
        dao.delAll()
        dao.insert(asteroids)
    }

    suspend fun getPictureOfDay() = Api.retrofitService.getPictureOfDay()
}