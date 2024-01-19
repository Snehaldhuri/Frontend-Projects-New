package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class Buttons(
    val buttonName: String = "",
    val forDisconnectedMode: Boolean = false
)