package com.diipl.moviebeam.data.kaping

data class CmdDataDto(
    val sessionId: String?,
    val parentSessionId: String?,
    val adultContentDisabled: Boolean?,
    val message: String?,
    val guestFirstName: String?,
    val guestLastName: String?,
    val adultLocked: Boolean?,
    val passcode: String?
)
