package com.diipl.moviebeam.data.dto.laundryResponce

data class LaundryResponse(
    val id: Int,
    val laundryDataList: List<LaundryRequestDTO>,
    val type: String,
    val version: String
)