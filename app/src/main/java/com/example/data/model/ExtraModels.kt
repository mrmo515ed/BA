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

data class AnimeItem(
    val id: String = "",
    val titleArabic: String = "",
    val titleEnglish: String = "",
    val synopsisArabic: String = "",
    val synopsisEnglish: String = "",
    val genres: List<String> = emptyList(),
    val status: String = "مستمر", // مستمر, مكتمل, قادم قريباً
    val releaseYear: Int = 2026,
    val season: String = "خريف 2026",
    val episodesCount: Int = 24,
    val rating: Double = 9.0,
    val coverImageUrl: String = "battle", // URL or preset (battle, character, banner)
    val bannerImageUrl: String = "banner",
    val addedBy: String = "",
    val addedByEmail: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class AdminAuditLog(
    val id: String = "",
    val adminId: String = "",
    val adminEmail: String = "",
    val actionType: String = "", // ADD_ANIME, EDIT_ANIME, DELETE_ANIME, BAN_USER, UNBAN_USER, RESOLVE_REPORT, DELETE_POST
    val targetId: String = "",
    val targetTitle: String = "",
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ReportItem(
    val id: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val targetId: String = "",
    val targetType: String = "POST", // POST, USER, MESSAGE, GROUP
    val reason: String = "",
    val status: String = "PENDING", // PENDING, RESOLVED, DISMISSED
    val actionTaken: String = "",
    val reviewedBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// Anime Tracking for User (watching, completed, plan to watch, episodes, rating) - Animesta & Kunaiu & Mirai feature
data class UserAnimeTracking(
    val animeId: String = "",
    val userId: String = "",
    val animeTitleArabic: String = "",
    val animeTitleEnglish: String = "",
    val coverImage: String = "battle",
    val status: String = "WATCHING", // WATCHING, COMPLETED, PLAN_TO_WATCH, ON_HOLD, DROPPED
    val currentEpisode: Int = 1,
    val totalEpisodes: Int = 24,
    val userScore: Double = 0.0,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

// Anime Episode Item (Discussions & Episode tracking)
data class AnimeEpisode(
    val episodeNumber: Int = 1,
    val titleArabic: String = "",
    val durationMinutes: Int = 24,
    val airDate: String = "",
    val discussionsCount: Int = 0
)
