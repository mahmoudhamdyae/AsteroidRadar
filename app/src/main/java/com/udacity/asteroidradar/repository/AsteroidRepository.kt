package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.Api
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.databse.AsteroidDatabase
import com.udacity.asteroidradar.databse.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * A playlist of movies that can be shown on the screen.
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.dao().getAsteroids()) {
            it.asDomainModel()
        }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the movies for use, observe [asteroids]
     */
    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val asteroidsList = Api.retrofitService.getAsteroidsAsync().await()

            val dao = database.dao()
            dao.delAll()
            dao.insertAll(*asteroidsList.asDatabaseModel())
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay {
        lateinit var pictureOfDay: PictureOfDay
        withContext(Dispatchers.IO) {
            pictureOfDay = Api.retrofitService.getPictureOfDayAsync().await()
        }
        return pictureOfDay
    }
}