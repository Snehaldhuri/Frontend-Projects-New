package com.diipl.moviebeam.data.dto.accountsetup

data class SelectedApps(
    val appText: String,
    val forDisconnectedMode: Boolean,
    val forLg: Boolean,
    val forPhilips: Boolean,
    val forSamsung: Boolean,
    val hdmi: Int,
    val id: Int,
    val label: String,
    val value: String,
    val valueId: Int
)