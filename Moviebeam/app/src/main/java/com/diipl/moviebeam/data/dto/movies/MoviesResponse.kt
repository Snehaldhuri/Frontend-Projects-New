package com.diipl.moviebeam.data.dto.movies

data class MoviesResponse(
    val accountId: String,
    val adultDayPassPrice: Int,
    val freeContentList: List<ContentDto>,
    val freeGenreList: List<GenreDto>,
    val id: Int,
    val premiumContentList: List<ContentDto>,
    val premiumGenreList: List<PremiumGenre>,
    val type: String,
    val version: String
)