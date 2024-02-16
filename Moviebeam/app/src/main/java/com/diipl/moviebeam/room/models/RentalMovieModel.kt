package com.diipl.moviebeam.room.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.Converters

@Entity(tableName = "recentMovies")
@TypeConverters(Converters::class)
data class RentalMovieModel(
    @PrimaryKey(autoGenerate = false) var rentalID: Int = 0,
    var startTimeStamp: Long = System.currentTimeMillis(),
    var sessionID: String = "",
    var cType: String = Constants.C_TYPE_MOVIE,
    var currentSeek : Long = 0,
    var finishTimeStamp: Long = startTimeStamp+(24*60*60*1000),
    var lastTimeStamp: Long = 0,
    @Embedded var movieData : ContentDto? = null
)