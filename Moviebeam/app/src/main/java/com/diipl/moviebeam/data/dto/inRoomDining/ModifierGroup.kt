package com.diipl.moviebeam.data.dto.inRoomDining

import com.diipl.moviebeam.data.dto.laundryResponce.LangWiseCategory

data class ModifierGroup(
    val isMandatory: Boolean,
    val isMultiple: Int,
    val langWiseList: Map<String, LangWiseCategory>,
    val modifierGroupId: Int,
    val modifierGroupName: String,
    val modifierItems: List<String>
)