package com.diipl.moviebeam.data.dto.inRoomDining

import com.diipl.moviebeam.data.dto.laundryResponce.LangWiseCategory

data class Items(
    val categoryId: Int,
    val categoryName: String,
    val desc: String,
    val dispPrice: String,
    val id: Int,
    val image: String,
    val imageCloud: String,
    val langWiseList: Map<String, LangWiseCategory>,
    val modifierGroupList: List<String>,
    val price: Double,
    val type: Int
)