//package com.udacity.asteroidradar.main
//
//import android.app.Application
//import android.provider.SyncStateContract.Helpers.insert
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewModelScope
//import com.google.gson.Gson
//import com.google.gson.JsonParser
//import com.udacity.asteroidradar.api.Api
//import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
//import com.udacity.asteroidradar.databse.AsteroidDao
//import com.udacity.asteroidradar.databse.AsteroidDatabase
//import com.udacity.asteroidradar.databse.getDatabase
//import com.udacity.asteroidradar.domain.Asteroid
//import com.udacity.asteroidradar.domain.PictureOfDay
//import com.udacity.asteroidradar.repository.AsteroidRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//
//class MainViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
//    val pictureOfDay: LiveData<PictureOfDay>
//        get() = _pictureOfDay
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String>
//        get() = _errorMessage
//
//    private val database = getDatabase(application)
//    private val asteroidRepository = AsteroidRepository(database)
//
//    private val _asteroids = MutableLiveData<List<Asteroid>>()
//    val asteroids: LiveData<List<Asteroid>>
//        get() = _asteroids
////    val asteroids = asteroidRepository.asteroids
//
//    private val asteroidDao: AsteroidDao by lazy {
//        getDatabase(application).asteroidDao()
//    }
//
//    init {
//        viewModelScope.launch {
//            val asteroids = getAsteroids()
////            _state.value = AsteroidState(false, asteroids)
//            _asteroids.value = asteroids
//
////            val pictureOfDay = getPicture()
////            _picture.value = PictureState(pictureOfDay)
//        }
//    }
//
////    init {
////        getAsteroids()
////        getPictureOfDay()
////    }
//
////    private fun getAsteroids() {
////        viewModelScope.launch {
////            try {
//////                asteroidRepository.refreshAsteroids()
////
//////                val response = Api.retrofitService.getAsteroids()
//////                val gson = JsonParser().parse(response.toString()).asJsonObject
//////
//////                val jo2 = JSONObject(gson.toString())
//////                val asteroids = parseAsteroidsJsonResult(jo2)
//////
//////                database.asteroidDao().insert(asteroids)
//////                database.asteroidDao().getAsteroids()
////
////                val gson = Gson()
////                val asteroids = Api.retrofitService.getAsteroids()
////
////            } catch (exception: Exception) {
////                _errorMessage.value = exception.toString()
////            }
////        }
////    }
//
//    private suspend fun getAsteroids(): List<Asteroid> = withContext(Dispatchers.IO) {
//        try {
//            val response = Api.retrofitService.getAsteroids()
//            val gson = JsonParser().parse(response.toString()).asJsonObject
//
//            val jo2 = JSONObject(gson.toString())
//            val asteroids = parseAsteroidsJsonResult(jo2)
//
//            asteroidDao.insert(asteroids)
//            asteroidDao.getAsteroids()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            asteroidDao.getAsteroids()
//        }
//    }
//
//    private fun getPictureOfDay() {
//        viewModelScope.launch {
//            try {
//                _pictureOfDay.value = asteroidRepository.refreshPictureOfDay()
//            }
//            catch (exception: Exception) {
//                _errorMessage.value = exception.toString()
//            }
//        }
//    }
//}
package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.JsonParser
import com.udacity.asteroidradar.api.Api
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.databse.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val _state: MutableStateFlow<AsteroidState> =
        MutableStateFlow(AsteroidState(true, emptyList()))

    val state = _state.asStateFlow()

    private val _picture: MutableLiveData<PictureState> =
        MutableLiveData(PictureState(null))

    val picture: LiveData<PictureState> = _picture

    private lateinit var cachedAsteroids: List<Asteroid>

    val loadingState = state.map { value -> value.loading }

    private val asteroidDao: AsteroidDao by lazy {
        getDatabase(app).asteroidDao()
    }

    private val pictureDao: PictureDao by lazy {
        getDatabase(app).pictureOfDayDao()
    }

    init {
        viewModelScope.launch {
            val asteroids = getAsteroids()
            _state.value = AsteroidState(false, asteroids)
            cachedAsteroids = asteroids

            val pictureOfDay = getPicture()
            _picture.value = PictureState(pictureOfDay)
        }
    }

    fun updateFilter(filter: ApiFilter) {
        viewModelScope.launch {
            when (filter) {
                ApiFilter.SHOW_WEEK -> {
                    val asteroids =
                        asteroidDao.getAsteroidsFromThisWeek(
                            DayProvider.getToday(),
                            DayProvider.getSevenDaysLater()
                        )
                    _state.value = AsteroidState(false, asteroids)
                }
                ApiFilter.SHOW_TODAY -> {
                    val asteroids = asteroidDao.getAsteroidToday(DayProvider.getToday())
                    _state.value = AsteroidState(false, asteroids)
                }
                else -> {
                    val asteroids = asteroidDao.getAsteroids()
                    _state.value = AsteroidState(false, asteroids)
                }
            }
        }
    }

    private suspend fun getAsteroids(): List<Asteroid> = withContext(Dispatchers.IO) {
        try {
            val response = Api.retrofitService.getAsteroids(
                DayProvider.getToday(),
                DayProvider.getSevenDaysLater()
            )
            val gson = JsonParser().parse(response.toString()).asJsonObject

            val jo2 = JSONObject(gson.toString())
            val asteroids = parseAsteroidsJsonResult(jo2)

            asteroidDao.insert(asteroids)
            asteroidDao.getAsteroids()
        } catch (e: Exception) {
            e.printStackTrace()
            asteroidDao.getAsteroids()
        }
    }

    private suspend fun getPicture(): PictureOfDay? = withContext(Dispatchers.IO) {
        try {
            val picture = Api.retrofitService.getPictureOfDay()
            pictureDao.insert(picture)
            pictureDao.getPicture(picture.url)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class AsteroidState(val loading: Boolean, val asteroids: List<Asteroid>)

data class PictureState(val picture: PictureOfDay?)

enum class ApiFilter(val value: String) { SHOW_WEEK("week"), SHOW_TODAY("today"), SHOW_SAVED("saved") }