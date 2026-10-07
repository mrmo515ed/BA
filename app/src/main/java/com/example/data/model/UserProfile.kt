package com.example.data.model

data class UserProfile(
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val bio: String = "أوتاكو ومتابع أنمي في بلاك انمي 🔥",
    val favoriteAnime: String = "هجوم العمالقة",
    val favoriteCharacter: String = "ليفاي أكرمان",
    val role: String = "أوتاكو مميز",
    val followersCount: Long = 0L,
    val followingCount: Long = 0L,
    val postsCount: Long = 0L,
    val joinedAt: Long = System.currentTimeMillis()
)
