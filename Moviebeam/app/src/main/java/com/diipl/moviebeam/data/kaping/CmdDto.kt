package com.diipl.moviebeam.data.kaping

data class CmdDto(
    var cmd: String?,
    var epochTime: String?,
    var transactionId: String?,
    var cmdData: CmdDataDto?
)