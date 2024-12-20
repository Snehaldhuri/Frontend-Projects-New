package com.diipl.moviebeam.data.dto.inRoomDining

import com.diipl.moviebeam.data.dto.laundryResponce.LangWiseCategory

data class Dining(
    val categoryId: Int,
    val categoryName: String,
    val defaultImage: String,
    val image: String,
    val infoText: String,
    val langWiseList: Map<String, LangWiseCategory>,
    val spotlightImage: String,
    val subMenuList: List<SubMenu>,
    val visible: Int
)