package com.example.data.model

data class Channel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val animeCategory: String = "عام",
    val animeSeries: String = "عام",
    val isBroadcastOnly: Boolean = false, // true = Channel for broadcast, false = Discussion Group
    val isPrivate: Boolean = false, // true = Private Group, false = Public
    val memberCount: Long = 1L,
    val members: List<String> = emptyList(),
    val admins: List<String> = emptyList(),
    val createdBy: String = "",
    val lastMessageText: String = "مرحباً بكم في القناة!",
    val lastMessageTime: Long = System.currentTimeMillis()
)
