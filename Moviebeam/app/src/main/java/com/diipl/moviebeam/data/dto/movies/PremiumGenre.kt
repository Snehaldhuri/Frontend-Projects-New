package com.diipl.moviebeam.data.dto.movies

import kotlinx.serialization.Serializable

@Serializable
data class PremiumGenre(
    val genreId: String = "",
    val genreName: String = "",
    val id: Int = 0,
    val languageGenreList: Map<String, GenreDto> = emptyMap()
)