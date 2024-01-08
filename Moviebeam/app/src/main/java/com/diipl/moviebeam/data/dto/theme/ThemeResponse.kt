package com.diipl.moviebeam.data.dto.theme

import kotlinx.serialization.Serializable

@Serializable
data class ThemeResponse(
    val accountId: Int = 0,
    val fontCss: String = "",
    val gradientColor: String = "",
    val id: Int = 0,
    val spotLightColor: String = "",
    val themeBackgroundFileName: String = "",
    val themeBackgroundFileNameCloud: String = "",
    val themeBgFileName: String = "",
    val themeBgFileNameCloud: String = "",
    val themeCss: String = "",
    val themeLogoFileName: String = "",
    val themeLogoFileNameCloud: String = "",
    val type: String = "",
    val version: String = ""
)