package com.example.data.model

data class Channel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val animeCategory: String = "عام",
    val memberCount: Long = 1L,
    val members: List<String> = emptyList(),
    val createdBy: String = "",
    val lastMessageText: String = "مرحباً بكم في القناة!",
    val lastMessageTime: Long = System.currentTimeMillis()
)
