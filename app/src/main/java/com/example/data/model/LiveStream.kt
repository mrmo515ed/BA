package com.example.data.model

data class LiveStream(
    val id: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val hostAvatarUrl: String = "",
    val title: String = "",
    val animeTopic: String = "عام",
    val viewersCount: Long = 1L,
    val isLive: Boolean = true,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val previewImage: String = "battle",
    val startedAt: Long = System.currentTimeMillis()
)

data class LiveComment(
    val id: String = "",
    val streamId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
