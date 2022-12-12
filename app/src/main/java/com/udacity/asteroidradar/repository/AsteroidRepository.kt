package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.google.gson.JsonParser
import com.udacity.asteroidradar.api.Api
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.databse.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * A playlist of movies that can be shown on the screen.
     */
    val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getAsteroids()
//        Transformations.map(database.dao().getAsteroids()) {
//            it.asDomainModel()
//        }
//    val pictureOfDay: LiveData<List<PictureOfDay>> =
//        database.pictureOfDayDao().getPictureOfDay()

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val asteroidsList = Api.retrofitService.getAsteroidsAsync()
            val gson = JsonParser().parse(asteroidsList.toString()).asJsonObject

            val jo2 = JSONObject(gson.toString())
            val asteroids = parseAsteroidsJsonResult(jo2)

            val dao = database.asteroidDao()
            dao.delAll()
            dao.insert(asteroids)
        }
    }

    suspend fun refreshPictureOfDay(): PictureOfDay {
        val pictureOfDay: PictureOfDay
        withContext(Dispatchers.IO) {
            pictureOfDay = Api.retrofitService.getPictureOfDayAsync().await()
        }
        return pictureOfDay
    }
}