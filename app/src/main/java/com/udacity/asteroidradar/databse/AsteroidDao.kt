package com.udacity.asteroidradar.databse

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asteroid: List<Asteroid>)

    @Query("SELECT * from asteroid ORDER BY closeApproachDate")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid WHERE closeApproachDate >= :startDay AND closeApproachDate <= :endDay ORDER BY closeApproachDate")
    fun getAsteroidsThisWeek(startDay: String, endDay: String): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid WHERE closeApproachDate = :today ")
    fun getAsteroidsToday(today: String): LiveData<List<Asteroid>>

    @Query("DELETE FROM asteroid")
    suspend fun delAll()
}