package com.diipl.moviebeam.data.dto.btn

data class BtnModel(
    val btnId: String = "",
    val imageResId: Int = 0,
    val title: String = "",
    var appPackageId: String = "",
    var isApp: Boolean = false
)
