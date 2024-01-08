package com.diipl.moviebeam.data.dto.datetime

import kotlinx.serialization.Serializable

@Serializable
data class DateTimeResponse(
    val accountId: String = "",
    val date: String = "",
    val day: String = "",
    val dstOffset: String = "",
    val hour: String = "",
    val minute: String = "",
    val month: String = "",
    val second: String = "",
    val time: String = "",
    val timezoneOffset: String = "",
    val type: String = "",
    val year: String = ""
)