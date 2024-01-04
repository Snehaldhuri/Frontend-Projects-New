package com.diipl.moviebeam.data.dto.showtime

import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val detailList: List<Detail> = emptyList(),
    val id: Int = 0,
    val name: String = "",
    val seasonId: Int = 0
)