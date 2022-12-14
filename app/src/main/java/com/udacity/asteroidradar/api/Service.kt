package com.udacity.asteroidradar.api

import com.google.gson.JsonObject
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit
    .Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface Service {

@GET("neo/rest/v1/feed/")
suspend fun getAsteroids(
    @Query("start_date") startDate: String,
    @Query("end_date") endDate: String,
    @Query("api_key") apiKey: String = Constants.API_KEY
): JsonObject

@GET("planetary/apod/")
suspend fun getPictureOfDay(
    @Query("api_key") apiKey: String = Constants.API_KEY
): PictureOfDay
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object Api {
    val retrofitService : Service by lazy { retrofit.create(Service::class.java) }
}