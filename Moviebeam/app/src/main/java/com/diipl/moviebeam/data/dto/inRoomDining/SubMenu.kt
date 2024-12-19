package com.diipl.moviebeam.data.dto.inRoomDining

import com.diipl.moviebeam.data.dto.laundryResponce.LangWiseCategory

data class SubMenu(
    val categoryId: Int,
    val categoryName: String,
    val itemsList: List<String>,
    val langWiseList: Map<String, LangWiseCategory>,
    val serveEndTime: String,
    val serveStartTime: String,
    val visible: Int
)