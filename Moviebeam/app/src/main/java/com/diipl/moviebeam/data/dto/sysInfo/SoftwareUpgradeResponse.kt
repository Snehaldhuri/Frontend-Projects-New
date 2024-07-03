package com.diipl.moviebeam.data.dto.sysInfo

data class SoftwareUpgradeResponse(
    val accountId: Int? = null,
    val fileCrc: String? = null,
    val fileName: String? = null,
    val fileSize: Int? = null,
    val isCurrent: Boolean = false,
    val softwareDownloadFtpUrl: String? = null,
    val softwareVersion: String? = null,
    val stbType: String? = null
)