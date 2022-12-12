package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.nasa.gov/"

private const val API_KEY = "LYdNmsl0aXazf9fqJRZHAEchmZtEj7LIXd6M7a4F"

/**
 * Build the Moshi object that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface Service {

    @GET("neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=$API_KEY")
    fun getAsteroidsAsync(): Deferred<NetworkEntitiesContainer>

    @GET("planetary/apod?api_key=$API_KEY")
    fun getPictureOfDayAsync(): Deferred<PictureOfDay>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object Api {
    val retrofitService : Service by lazy { retrofit.create(Service::class.java) }
}