package com.diipl.moviebeam.data.dto.showtime

data class Season(
    val detailList: List<Detail>,
    val id: Int,
    val name: String,
    val seasonId: Int
)