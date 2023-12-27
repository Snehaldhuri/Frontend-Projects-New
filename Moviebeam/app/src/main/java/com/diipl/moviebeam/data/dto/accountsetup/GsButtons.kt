package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class GsButtons(
    val buttonName: String = "",
    val forDisconnectedMode: Boolean = false
)