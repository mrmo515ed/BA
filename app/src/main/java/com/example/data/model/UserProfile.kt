package com.example.data.model

data class UserProfile(
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val bio: String = "أوتاكو ومتابع أنمي في بلاك انمي 🔥",
    val favoriteAnime: String = "هجوم العمالقة",
    val favoriteCharacter: String = "ليفاي أكرمان",
    val favoriteAnimeList: List<String> = listOf("هجوم العمالقة", "ون بيس", "جوجوتسو كايسن", "سولو ليفلينغ"),
    val role: String = "أوتاكو مميز",
    val followersCount: Long = 0L,
    val followingCount: Long = 0L,
    val postsCount: Long = 0L,
    val friendsCount: Long = 0L,
    val friends: List<String> = emptyList(),
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),
    val joinedAt: Long = System.currentTimeMillis()
)
