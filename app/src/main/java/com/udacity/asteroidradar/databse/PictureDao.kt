package com.udacity.asteroidradar.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.domain.PictureOfDay

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pictureOfDay: PictureOfDay)

    @Query("SELECT * from picture_table WHERE url=:url")
    suspend fun getPicture(url: String): PictureOfDay

}