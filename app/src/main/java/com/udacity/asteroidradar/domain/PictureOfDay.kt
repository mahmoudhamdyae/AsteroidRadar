package com.udacity.asteroidradar.domain

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

//data class PictureOfDay(
//    @Json(name = "media_type") val mediaType: String,
//    val title: String,
//    val url: String
//)

@Entity(tableName = "picture_table")
@Parcelize
data class PictureOfDay(
    @ColumnInfo(name = "media_type") val mediaType: String? = "",
    @ColumnInfo(name = "title") val title: String? = "",
    @PrimaryKey val url: String
) : Parcelable