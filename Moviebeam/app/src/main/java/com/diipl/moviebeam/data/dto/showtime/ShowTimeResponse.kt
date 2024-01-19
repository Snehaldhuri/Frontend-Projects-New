package com.diipl.moviebeam.data.dto.showtime

import kotlinx.serialization.Serializable

@Serializable
data class ShowTimeResponse(
    val accountId: Int = 0,
    val id: Int = 0,
    val shoContentList: List<ShowTimeContent> = emptyList(),
    val shoGenreList: List<ShowTimeGenre> = emptyList(),
    val type: String = "",
    val version: String = ""
)