package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class SelectedApps(
    val appText: String = "",
    val forDisconnectedMode: Boolean = false,
    val forLg: Boolean = false,
    val forPhilips: Boolean = false,
    val forSamsung: Boolean = false,
    val hdmi: Int = 0,
    val id: Int = 0,
    val label: String = "",
    val value: String = "",
    val valueId: Int = 0
)