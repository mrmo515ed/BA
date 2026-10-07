package com.example.data.model

data class FriendRequest(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val receiverId: String = "",
    val status: String = "PENDING", // PENDING, ACCEPTED, DECLINED
    val timestamp: Long = System.currentTimeMillis()
)
