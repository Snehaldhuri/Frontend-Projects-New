package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class Languages(
    val languageCode: String = "",
    val languageId: Int = 0,
    val languageName: String = "",
    val regionCode: String = ""
)