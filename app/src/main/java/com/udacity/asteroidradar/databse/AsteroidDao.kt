package com.udacity.asteroidradar.databse

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.domain.Asteroid

@androidx.room.Dao
interface AsteroidDao {

    @Query("SELECT * FROM asteroid")
    fun getAsteroids() : LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(roomAsteroid: List<RoomAsteroid>)
    suspend fun insert(asteroid: List<Asteroid>)

    @Query("DELETE FROM asteroid")
    suspend fun delAll()
}