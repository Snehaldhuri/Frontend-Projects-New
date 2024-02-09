package com.diipl.moviebeam.data.kaping

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CmdDto(
    var cmd: String?,
    var epochTime: String?,
    var transactionId: String?,
    var cmdData: CmdDataDto?
) : Parcelable