package com.udacity.asteroidradar.api

import com.google.gson.JsonObject
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.nasa.gov/"

private const val API_KEY = "LYdNmsl0aXazf9fqJRZHAEchmZtEj7LIXd6M7a4F"

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit
    .Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface Service {

//    @GET("neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=$API_KEY")
//    suspend fun getAsteroids(): JsonObject
@GET("neo/rest/v1/feed/")
suspend fun getAsteroids(
    @Query("start_date") startDate: String,
    @Query("end_date") endDate: String,
    @Query("api_key") apiKey: String = Constants.API_KEY
): JsonObject

//    @GET("planetary/apod?api_key=$API_KEY")
//    fun getPictureOfDayAsync(): Deferred<PictureOfDay>
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