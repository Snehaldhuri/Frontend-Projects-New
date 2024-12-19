package com.diipl.moviebeam.data.dto.inRoomDining

data class InRoomDining(
    val diningGuestMsg: DiningGuestMsg,
    val diningList: List<Dining>,
    val id: Int,
    val itemsList: List<Items>,
    val modifierGroupList: List<ModifierGroup>,
    val type: String,
    val version: String
)