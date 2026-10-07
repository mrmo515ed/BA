package com.example.data.model

data class Story(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val animeTag: String = "",
    val viewedBy: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
