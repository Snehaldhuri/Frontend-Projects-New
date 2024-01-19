package com.diipl.moviebeam.data.dto.movies

import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponse(
    val accountId: String = "",
    val adultDayPassPrice: Int = 0,
    val freeContentList: List<ContentDto> = emptyList(),
    val freeGenreList: List<GenreDto> = emptyList(),
    val id: Int = 0,
    val premiumContentList: List<ContentDto> = emptyList(),
    val premiumGenreList: List<PremiumGenre> = emptyList(),
    val type: String = "",
    val version: String = ""
)