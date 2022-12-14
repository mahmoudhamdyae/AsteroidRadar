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

    private val _picture = MutableLiveData<PictureOfDay>()
    val picture: LiveData<PictureOfDay>
        get() = _picture

    private lateinit var cachedAsteroids: List<Asteroid>

    val loadingState = state.map { value -> value.loading }

    private val asteroidDao: AsteroidDao by lazy {
        getDatabase(application).asteroidDao()
    }

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    init {
        getPicture()
        viewModelScope.launch {
            val asteroids = getAsteroids()
            _state.value = AsteroidState(false, asteroids)
            cachedAsteroids = asteroids
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

    private fun getPicture() {
        viewModelScope.launch {
            try {
                _picture.value = asteroidRepository.getPictureOfDay()
            } catch (exception: Exception) {
                _errorMessage.value = exception.toString()
            }
        }
    }
}

data class AsteroidState(val loading: Boolean, val asteroids: List<Asteroid>)

enum class ApiFilter(val value: String) { SHOW_WEEK("week"), SHOW_TODAY("today"), SHOW_SAVED("saved") }