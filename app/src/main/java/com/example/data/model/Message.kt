package com.example.data.model

data class Message(
    val id: String = "",
    val channelId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val content: String = "",
    val mediaUrl: String = "",
    val messageType: String = "TEXT", // TEXT, IMAGE, VOICE, ANIME_STICKER
    val replyToMessageId: String = "",
    val replyToSenderName: String = "",
    val replyToContent: String = "",
    val reactions: Map<String, List<String>> = emptyMap(), // emoji -> list of userIds
    val timestamp: Long = System.currentTimeMillis()
)
