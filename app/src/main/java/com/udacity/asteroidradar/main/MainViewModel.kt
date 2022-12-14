package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.databse.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class AsteroidState { LOADING, ERROR, DONE }
enum class ApiFilter(val value: String) {
    SHOW_WEEK("week"), SHOW_TODAY("today"), SHOW_SAVED("saved")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroids = asteroidRepository.asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _state = MutableLiveData<AsteroidState>()
    val state: LiveData<AsteroidState>
        get() = _state

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    init {
        getPicture()
        getAsteroids()
    }

    fun updateFilter(filter: ApiFilter) {
//        viewModelScope.launch {
//            when (filter) {
//                ApiFilter.SHOW_WEEK -> {
//                    val asteroids =
//                        asteroidDao.getAsteroidsFromThisWeek(
//                            DayProvider.getToday(),
//                            DayProvider.getSevenDaysLater()
//                        )
//                    _state.value = AsteroidState(false, asteroids)
//                }
//                ApiFilter.SHOW_TODAY -> {
//                    val asteroids = asteroidDao.getAsteroidToday(DayProvider.getToday())
//                    _state.value = AsteroidState(false, asteroids)
//                }
//                else -> {
//                    val asteroids = asteroidDao.getAsteroids()
//                    _state.value = AsteroidState(false, asteroids)
//                }
//            }
//        }
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                _state.value = AsteroidState.LOADING
                asteroidRepository.refreshAsteroids()
                _state.value = AsteroidState.DONE
//                val a = asteroidDao.getAsteroids()
//                _state.value = AsteroidState(false, a)
//                cachedAsteroids = a
            } catch (exception: Exception) {
                if (asteroids.value!!.isEmpty()) {
                    _state.value = AsteroidState.ERROR
                }
                _errorMessage.value = exception.toString()
//                exception.printStackTrace()
//                val a = asteroidDao.getAsteroids()
//                _state.value = AsteroidState(false, a)
//                cachedAsteroids = a
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