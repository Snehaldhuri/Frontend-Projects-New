package com.diipl.moviebeam.data.dto.message

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Int? = null,
    val isRead: Int? = null,
    val messageDate: String? = null,
    val messageId: Int? = null,
    val messagesList: List<MessageResponse>? = null,
    val messageSubject: String? = null,
    val messageText: String? = null,
    val type: String? = null,
    val userId: Int? = null
)