package com.application.models

import java.util.Date

data class MessageModel(
    val senderId: String = "",
    val messageText: String = "",
    val timestamp: Date = Date()
)