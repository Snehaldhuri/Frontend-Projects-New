package com.diipl.moviebeam.data.dto.showtime

import kotlinx.serialization.Serializable

@Serializable
data class ShowTimeGenre(
    val detailList: List<Detail>  = emptyList(),
    val id: Int = 0,
    val name: String = ""
)