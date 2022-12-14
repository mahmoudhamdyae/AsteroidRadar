package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.Api
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.databse.AsteroidDatabase
import com.udacity.asteroidradar.databse.DayProvider
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class AsteroidRepository(private val database: AsteroidDatabase) {

//        val asteroids: LiveData<List<Asteroid>> =
//        database.asteroidDao().getAsteroids()
//        Transformations.map(database.dao().getAsteroids()) {
//            it.asDomainModel()
//        }

    suspend fun refreshAsteroids() {
    }

    suspend fun getPictureOfDay() = Api.retrofitService.getPictureOfDay()
}