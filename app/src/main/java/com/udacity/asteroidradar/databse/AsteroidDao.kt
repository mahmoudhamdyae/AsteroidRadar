package com.udacity.asteroidradar.databse

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.domain.Asteroid

//@androidx.room.Dao
//interface AsteroidDao {
//
//    @Query("SELECT * FROM asteroid")
//    fun getAsteroids() : List<Asteroid>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
////    suspend fun insertAll(roomAsteroid: List<RoomAsteroid>)
//    suspend fun insert(asteroid: List<Asteroid>)
//
//    @Query("DELETE FROM asteroid")
//    suspend fun delAll()
//}

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asteroid: List<Asteroid>)

    @Query("SELECT * from asteroid_table")
    suspend fun getAsteroids(): List<Asteroid>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate >= :startDay AND closeApproachDate <= :endDay ORDER BY closeApproachDate")
    suspend fun getAsteroidsFromThisWeek(startDay: String, endDay: String): List<Asteroid>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate = :today ")
    suspend fun getAsteroidToday(today: String): List<Asteroid>
}