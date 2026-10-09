package com.example.data.model

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val authorRole: String = "أوتاكو مميز",
    val content: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "IMAGE", // IMAGE, REEL, QUOTE, DISCUSSION, NEWS
    val animeTitle: String = "",
    val tags: List<String> = emptyList(),
    val likesCount: Long = 0L,
    val likedBy: List<String> = emptyList(),
    val commentsCount: Long = 0L,
    val sharesCount: Long = 0L,
    val savedBy: List<String> = emptyList(),
    val isNews: Boolean = false,
    val isSpoiler: Boolean = false,
    val pollQuestion: String = "",
    val pollOptions: List<String> = emptyList(),
    val pollVotes: Map<String, Int> = emptyMap(), // optionIndex -> votesCount
    val votedUsers: Map<String, Int> = emptyMap(), // userId -> optionIndex
    val ratingScore: Double = 0.0,
    val groupId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
