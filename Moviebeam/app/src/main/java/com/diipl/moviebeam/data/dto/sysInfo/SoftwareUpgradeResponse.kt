package com.diipl.moviebeam.data.dto.sysInfo

import com.google.gson.annotations.SerializedName

data class SoftwareUpgradeResponse(
    @SerializedName("fileCrc")
    var fileCrc: String = "",
    @SerializedName("fileName")
    var fileName: String = "",
    @SerializedName("fileSize")
    var fileSize: String = "",
    @SerializedName("isCurrent")
    var isCurrent: Boolean = false,
    @SerializedName("softwareDownloadFtpUrl")
    var softwareDownloadFtpUrl: String = "",
    @SerializedName("softwareVersion")
    var softwareVersion: String = "",
    @SerializedName("stbType")
    var stbType: String = ""
)