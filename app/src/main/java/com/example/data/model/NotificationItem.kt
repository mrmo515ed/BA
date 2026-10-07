package com.example.data.model

data class NotificationItem(
    val id: String = "",
    val recipientId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val type: String = "LIKE", // LIKE, COMMENT, FOLLOW, ANNOUNCEMENT, MESSAGE
    val title: String = "",
    val message: String = "",
    val targetId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
