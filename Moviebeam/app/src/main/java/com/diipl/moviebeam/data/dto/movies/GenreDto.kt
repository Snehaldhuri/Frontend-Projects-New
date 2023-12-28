package com.diipl.moviebeam.data.dto.movies

data class GenreDto(
    val genreId: String,
    val genreName: String,
    val id: Int,
    val languageGenreList: Map<String, GenreDto>
)