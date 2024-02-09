package com.diipl.moviebeam.data.kaping

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CmdDataDto(
    val sessionId: String? = null,
    val parentSessionId: String? = null,
    val adultContentDisabled: Boolean? = false,
    val message: String? = null,
    val guestFirstName: String? = null,
    val guestLastName: String? = null,
    val adultLocked: Boolean? = false,
    val passcode: String? = null
) : Parcelable
