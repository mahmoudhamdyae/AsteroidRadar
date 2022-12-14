package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.databse.DayProvider
import com.udacity.asteroidradar.databse.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class AsteroidState { LOADING, ERROR, DONE }
enum class ApiFilter {
    SHOW_WEEK, SHOW_TODAY, SHOW_SAVED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    var asteroids: LiveData<List<Asteroid>> = asteroidRepository.asteroidsThisWeek

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
        viewModelScope.launch {
            when (filter) {
                ApiFilter.SHOW_WEEK -> {
                    _state.value = AsteroidState.LOADING
                    asteroidRepository.refreshAsteroids()
                    asteroids = asteroidRepository.asteroidsThisWeek
                    _state.value = AsteroidState.DONE
                }
                ApiFilter.SHOW_TODAY -> {
                    _state.value = AsteroidState.LOADING
                    asteroidRepository.refreshAsteroids(
                        startDate = DayProvider.getToday(),
                        endDate = DayProvider.getToday()
                    )
                    asteroids = asteroidRepository.asteroidsToday
                    _state.value = AsteroidState.DONE
                }
                else -> {
                    _state.value = AsteroidState.LOADING
                    asteroidRepository.refreshAsteroids()
                    asteroids = asteroidRepository.asteroidsAll
                    _state.value = AsteroidState.DONE
                }
            }
        }
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                _state.value = AsteroidState.LOADING
                asteroidRepository.refreshAsteroids()
                asteroids = asteroidRepository.asteroidsAll
                _state.value = AsteroidState.DONE
            } catch (exception: Exception) {
                if (asteroids.value!!.isEmpty()) {
                    _state.value = AsteroidState.ERROR
                }
                _errorMessage.value = exception.toString()
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