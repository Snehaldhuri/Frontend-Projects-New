package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class Concierge(
    val serviceId: Int = 0,
    val serviceJsonName: String = ""
)