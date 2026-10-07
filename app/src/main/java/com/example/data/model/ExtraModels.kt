package com.example.data.model

data class ReelItem(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val videoPreviewRes: String = "battle", // battle, character, banner
    val caption: String = "",
    val musicTitle: String = "Anime Opening OST",
    val animeTitle: String = "",
    val likesCount: Long = 0L,
    val likedBy: List<String> = emptyList(),
    val commentsCount: Long = 0L,
    val sharesCount: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

data class EconomyProfile(
    val userId: String = "",
    val coins: Long = 500L,
    val gems: Long = 25L,
    val xp: Long = 120L,
    val level: Int = 3,
    val dailyStreak: Int = 1,
    val lastDailyRewardTimestamp: Long = 0L,
    val unlockedTitles: List<String> = listOf("مبتدئ الأوتاكو", "عاشق الشونين"),
    val characterCards: List<String> = listOf("ليفاي أكرمان", "غوجو ساتورو", "سون غوكو")
)

data class AnimeWikiItem(
    val id: String = "",
    val titleArabic: String = "",
    val titleRomaji: String = "",
    val synopsis: String = "",
    val genres: List<String> = emptyList(),
    val episodesCount: Int = 24,
    val rating: Double = 9.0,
    val status: String = "مستمر",
    val season: String = "خريف 2026",
    val coverImage: String = "character"
)

data class ReportItem(
    val id: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val targetId: String = "",
    val targetType: String = "POST", // POST, USER, MESSAGE, GROUP
    val reason: String = "",
    val status: String = "PENDING", // PENDING, RESOLVED
    val timestamp: Long = System.currentTimeMillis()
)
