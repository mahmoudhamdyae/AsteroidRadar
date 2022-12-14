package com.udacity.asteroidradar.main

import com.udacity.asteroidradar.repository.AsteroidRepository

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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _state: MutableStateFlow<AsteroidState> =
        MutableStateFlow(AsteroidState(true, emptyList()))
    val state = _state.asStateFlow()

    private lateinit var cachedAsteroids: List<Asteroid>
    val loadingState = state.map { value -> value.loading }

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    private val asteroidDao = database.asteroidDao()

    init {
        getPicture()
        getAsteroids()
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

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                val response = Api.retrofitService.getAsteroids(
                    DayProvider.getToday(),
                    DayProvider.getSevenDaysLater()
                )
                val gson = JsonParser().parse(response.toString()).asJsonObject

                val jo2 = JSONObject(gson.toString())
                val asteroids = parseAsteroidsJsonResult(jo2)

                asteroidDao.insert(asteroids)
                val a = asteroidDao.getAsteroids()
                _state.value = AsteroidState(false, a)
                cachedAsteroids = a
            } catch (e: Exception) {
                e.printStackTrace()
                val a = asteroidDao.getAsteroids()
                _state.value = AsteroidState(false, a)
                cachedAsteroids = a
            }
        }
    }

    private fun getPicture() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = asteroidRepository.getPictureOfDay()
            } catch (exception: Exception) {
                _errorMessage.value = exception.toString()
            }
        }
    }
}

data class AsteroidState(val loading: Boolean, val asteroids: List<Asteroid>)

enum class ApiFilter(val value: String) { SHOW_WEEK("week"), SHOW_TODAY("today"), SHOW_SAVED("saved") }