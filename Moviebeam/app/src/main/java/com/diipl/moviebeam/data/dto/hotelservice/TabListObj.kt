package com.diipl.moviebeam.data.dto.hotelservice

data class TabListObj(
    val serviceType: Int,
    val service: Service?,
    val serviceList: List<Service>?
)