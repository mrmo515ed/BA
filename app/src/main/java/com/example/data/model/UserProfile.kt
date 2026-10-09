package com.example.data.model

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val bio: String = "أوتاكو ومتابع أنمي في بلاك انمي 🔥",
    val favoriteAnime: String = "هجوم العمالقة",
    val favoriteCharacter: String = "ليفاي أكرمان",
    val favoriteAnimeList: List<String> = listOf("هجوم العمالقة", "ون بيس", "جوجوتسو كايسن", "سولو ليفلينغ"),
    val role: String = "أوتاكو مميز",
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false,
    val banReason: String = "",
    val followersCount: Long = 0L,
    val followingCount: Long = 0L,
    val postsCount: Long = 0L,
    val friendsCount: Long = 0L,
    val friends: List<String> = emptyList(),
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),
    val joinedAt: Long = System.currentTimeMillis()
) {
    fun hasAdminPrivileges(currentUserEmail: String? = null): Boolean {
        val emailToCheck = currentUserEmail?.trim() ?: email.trim()
        return isAdmin ||
               emailToCheck.equals("m774545471@gmail.com", ignoreCase = true) ||
               role.contains("مدير", ignoreCase = true) ||
               role.contains("admin", ignoreCase = true)
    }
}
