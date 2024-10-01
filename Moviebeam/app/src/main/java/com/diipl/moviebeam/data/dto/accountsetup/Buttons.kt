package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class Buttons(
    val buttonName: String = "",
    val forDisconnectedMode: Boolean = false,
    val sortOrder: String?,
    val isApp: Boolean? = null,
    val appPackageId: String? = null
)