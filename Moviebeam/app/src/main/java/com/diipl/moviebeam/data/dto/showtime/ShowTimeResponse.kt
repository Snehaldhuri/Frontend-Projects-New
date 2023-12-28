package com.diipl.moviebeam.data.dto.showtime

data class ShowTimeResponse(
    val accountId: Int,
    val id: Int,
    val shoContentList: List<ShowTimeContent>,
    val shoGenreList: List<ShowTimeGenre>,
    val type: String,
    val version: String
)