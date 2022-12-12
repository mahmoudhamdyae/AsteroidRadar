package com.udacity.asteroidradar.databse

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@androidx.room.Dao
interface Dao {

    @Query("SELECT * FROM roomasteroid")
    fun getAsteroids() : LiveData<List<RoomAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg roomAsteroid: RoomAsteroid)

    @Query("DELETE FROM roomasteroid")
    suspend fun delAll()
}