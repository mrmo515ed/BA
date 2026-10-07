package com.example.data.model

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val authorRole: String = "أوتاكو مميز",
    val content: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "IMAGE", // IMAGE, REEL, QUOTE, DISCUSSION
    val animeTitle: String = "",
    val tags: List<String> = emptyList(),
    val likesCount: Long = 0L,
    val likedBy: List<String> = emptyList(),
    val commentsCount: Long = 0L,
    val savedBy: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
