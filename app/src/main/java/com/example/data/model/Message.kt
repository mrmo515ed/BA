package com.example.data.model

data class Message(
    val id: String = "",
    val channelId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
