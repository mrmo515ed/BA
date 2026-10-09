package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.AdminAuditLog
import com.example.data.model.AnimeItem
import com.example.data.model.Channel
import com.example.data.model.Comment
import com.example.data.model.FriendRequest
import com.example.data.model.LiveComment
import com.example.data.model.LiveStream
import com.example.data.model.Message
import com.example.data.model.NotificationItem
import com.example.data.model.Post
import com.example.data.model.ReportItem
import com.example.data.model.Story
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val TAG = "AnimeRepository"

class AnimeRepository(
    private val db: FirebaseFirestore
) {
    init {
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                )
                .build()
            db.firestoreSettings = settings
        } catch (_: Exception) {
            // Settings already applied or instance active
        }
    }
    constructor(context: Context) : this(
        try {
            val dbId = context.applicationContext.getString(R.string.firestore_database_id)
            if (dbId.isNotBlank() && dbId != "(default)") {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Fallback to default Firestore: ${e.message}")
            FirebaseFirestore.getInstance()
        }
    )

    // User Profile
    suspend fun getOrCreateUserProfile(user: FirebaseUser): UserProfile {
        val docRef = db.collection("users").document(user.uid)
        val isSystemAdmin = user.email?.equals("m774545471@gmail.com", ignoreCase = true) == true
        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            val profile = snapshot.toObject(UserProfile::class.java)
            if (profile != null) {
                if (isSystemAdmin && (!profile.isAdmin || profile.email != user.email)) {
                    val updatedProfile = profile.copy(
                        isAdmin = true,
                        email = user.email ?: "",
                        role = if (profile.role.contains("مدير") || profile.role.contains("admin", true)) profile.role else "المدير العام للمنصة 👑"
                    )
                    docRef.set(updatedProfile).await()
                    return updatedProfile
                }
                return profile
            }
        }

        val cleanUsername = (user.email?.substringBefore("@") ?: "otaku_${user.uid.take(5)}")
        val newProfile = UserProfile(
            userId = user.uid,
            email = user.email ?: "",
            username = cleanUsername,
            displayName = user.displayName ?: "محارب الأنمي",
            avatarUrl = user.photoUrl?.toString() ?: "",
            bio = "أوتاكو ومتابع أنمي في بلاك انمي 🔥",
            favoriteAnime = "هجوم العمالقة",
            favoriteCharacter = "ليفاي أكرمان",
            role = if (isSystemAdmin) "المدير العام للمنصة 👑" else "أوتاكو مميز",
            isAdmin = isSystemAdmin,
            joinedAt = System.currentTimeMillis()
        )
        docRef.set(newProfile).await()
        return newProfile
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        return db.collection("users").document(userId)
            .snapshots()
            .map { it.toObject(UserProfile::class.java) }
    }

    fun observeAllUsers(): Flow<List<UserProfile>> {
        return db.collection("users")
            .limit(30)
            .snapshots()
            .map { it.toObjects(UserProfile::class.java) }
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        db.collection("users").document(profile.userId)
            .set(profile)
            .await()
    }

    // Follow / Unfollow System
    suspend fun followUser(currentUserId: String, targetUserId: String) {
        if (currentUserId == targetUserId) return
        db.runTransaction { transaction ->
            val currentUserRef = db.collection("users").document(currentUserId)
            val targetUserRef = db.collection("users").document(targetUserId)

            transaction.update(currentUserRef, "following", FieldValue.arrayUnion(targetUserId))
            transaction.update(currentUserRef, "followingCount", FieldValue.increment(1))

            transaction.update(targetUserRef, "followers", FieldValue.arrayUnion(currentUserId))
            transaction.update(targetUserRef, "followersCount", FieldValue.increment(1))
        }.await()
    }

    suspend fun unfollowUser(currentUserId: String, targetUserId: String) {
        db.runTransaction { transaction ->
            val currentUserRef = db.collection("users").document(currentUserId)
            val targetUserRef = db.collection("users").document(targetUserId)

            transaction.update(currentUserRef, "following", FieldValue.arrayRemove(targetUserId))
            transaction.update(currentUserRef, "followingCount", FieldValue.increment(-1))

            transaction.update(targetUserRef, "followers", FieldValue.arrayRemove(currentUserId))
            transaction.update(targetUserRef, "followersCount", FieldValue.increment(-1))
        }.await()
    }

    // Friend Requests System
    fun observeFriendRequests(userId: String): Flow<List<FriendRequest>> {
        return db.collection("friend_requests")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("status", "PENDING")
            .snapshots()
            .map { it.toObjects(FriendRequest::class.java) }
    }

    suspend fun sendFriendRequest(request: FriendRequest) {
        val docRef = db.collection("friend_requests").document()
        docRef.set(request.copy(id = docRef.id)).await()
    }

    suspend fun respondToFriendRequest(requestId: String, accept: Boolean, senderId: String, receiverId: String) {
        val status = if (accept) "ACCEPTED" else "DECLINED"
        db.collection("friend_requests").document(requestId).update("status", status).await()

        if (accept) {
            // Add mutual friends
            db.collection("users").document(senderId).update(
                "friends", FieldValue.arrayUnion(receiverId),
                "friendsCount", FieldValue.increment(1)
            ).await()

            db.collection("users").document(receiverId).update(
                "friends", FieldValue.arrayUnion(senderId),
                "friendsCount", FieldValue.increment(1)
            ).await()
        }
    }

    // Posts & Feed
    fun observePosts(): Flow<List<Post>> {
        return db.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Post::class.java)
            }
    }

    suspend fun createPost(post: Post) {
        val docRef = if (post.id.isEmpty()) db.collection("posts").document() else db.collection("posts").document(post.id)
        val finalPost = post.copy(id = docRef.id)
        docRef.set(finalPost).await()

        try {
            db.collection("users").document(post.authorId)
                .update("postsCount", FieldValue.increment(1))
                .await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update postsCount: ${e.message}")
        }
    }

    suspend fun toggleLikePost(postId: String, userId: String, userName: String = "") {
        val docRef = db.collection("posts").document(postId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val post = snapshot.toObject(Post::class.java) ?: return@runTransaction
            val likedList = post.likedBy.toMutableList()
            val isLiked = likedList.contains(userId)

            val newLikesCount: Long
            if (isLiked) {
                likedList.remove(userId)
                newLikesCount = (post.likesCount - 1).coerceAtLeast(0L)
            } else {
                likedList.add(userId)
                newLikesCount = post.likesCount + 1
            }

            transaction.update(docRef, "likedBy", likedList)
            transaction.update(docRef, "likesCount", newLikesCount)
        }.await()
    }

    suspend fun reSharePost(postId: String) {
        db.collection("posts").document(postId)
            .update("sharesCount", FieldValue.increment(1))
            .await()
    }

    // Comments
    fun observeComments(postId: String): Flow<List<Comment>> {
        return db.collection("posts").document(postId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(Comment::class.java) }
    }

    suspend fun addComment(comment: Comment) {
        val postDocRef = db.collection("posts").document(comment.postId)
        val commentDocRef = postDocRef.collection("comments").document()
        val finalComment = comment.copy(id = commentDocRef.id)

        commentDocRef.set(finalComment).await()
        postDocRef.update("commentsCount", FieldValue.increment(1)).await()
    }

    // Stories
    fun observeStories(): Flow<List<Story>> {
        return db.collection("stories")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(Story::class.java) }
    }

    suspend fun createStory(story: Story) {
        val docRef = db.collection("stories").document()
        val finalStory = story.copy(id = docRef.id)
        docRef.set(finalStory).await()
    }

    // Live Streaming
    fun observeLiveStreams(): Flow<List<LiveStream>> {
        return db.collection("live_streams")
            .whereEqualTo("isLive", true)
            .orderBy("startedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(LiveStream::class.java) }
    }

    suspend fun startLiveStream(stream: LiveStream): String {
        val docRef = db.collection("live_streams").document()
        val finalStream = stream.copy(id = docRef.id)
        docRef.set(finalStream).await()
        return docRef.id
    }

    suspend fun updateLiveControls(streamId: String, isMuted: Boolean, isCameraOff: Boolean) {
        db.collection("live_streams").document(streamId).update(
            "isMuted", isMuted,
            "isCameraOff", isCameraOff
        ).await()
    }

    suspend fun endLiveStream(streamId: String) {
        db.collection("live_streams").document(streamId).update(
            "isLive", false
        ).await()
    }

    fun observeLiveComments(streamId: String): Flow<List<LiveComment>> {
        return db.collection("live_streams").document(streamId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(LiveComment::class.java) }
    }

    suspend fun sendLiveComment(comment: LiveComment) {
        val docRef = db.collection("live_streams").document(comment.streamId)
            .collection("comments").document()
        docRef.set(comment.copy(id = docRef.id)).await()
    }

    // Channels & Groups
    fun observeChannels(): Flow<List<Channel>> {
        return db.collection("channels")
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(Channel::class.java) }
    }

    suspend fun createChannel(channel: Channel): String {
        val docRef = db.collection("channels").document()
        val finalChannel = channel.copy(id = docRef.id)
        docRef.set(finalChannel).await()
        return docRef.id
    }

    suspend fun joinChannel(channelId: String, userId: String) {
        val docRef = db.collection("channels").document(channelId)
        docRef.update(
            "members", FieldValue.arrayUnion(userId),
            "memberCount", FieldValue.increment(1)
        ).await()
    }

    // Messages
    fun observeMessages(channelId: String): Flow<List<Message>> {
        return db.collection("channels").document(channelId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(Message::class.java) }
    }

    suspend fun sendMessage(message: Message) {
        val channelDocRef = db.collection("channels").document(message.channelId)
        val msgDocRef = channelDocRef.collection("messages").document()
        val finalMsg = message.copy(id = msgDocRef.id)

        msgDocRef.set(finalMsg).await()
        channelDocRef.update(
            "lastMessageText", message.content,
            "lastMessageTime", message.timestamp
        ).await()
    }

    // Notifications
    fun observeNotifications(userId: String): Flow<List<NotificationItem>> {
        return db.collection("notifications")
            .whereEqualTo("recipientId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(NotificationItem::class.java) }
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        db.collection("notifications").document(notificationId)
            .update("isRead", true)
            .await()
    }

    suspend fun markAllNotificationsAsRead(userId: String) {
        val snapshot = db.collection("notifications")
            .whereEqualTo("recipientId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        val batch = db.batch()
        for (doc in snapshot.documents) {
            batch.update(doc.reference, "isRead", true)
        }
        batch.commit().await()
    }

    // Reels
    fun observeReels(): Flow<List<com.example.data.model.ReelItem>> {
        return db.collection("reels")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(com.example.data.model.ReelItem::class.java) }
    }

    suspend fun createReel(reel: com.example.data.model.ReelItem) {
        val docRef = db.collection("reels").document()
        docRef.set(reel.copy(id = docRef.id)).await()
    }

    suspend fun toggleLikeReel(reelId: String, userId: String) {
        val docRef = db.collection("reels").document(reelId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val reel = snapshot.toObject(com.example.data.model.ReelItem::class.java) ?: return@runTransaction
            val liked = reel.likedBy.toMutableList()
            val newCount = if (liked.contains(userId)) {
                liked.remove(userId)
                (reel.likesCount - 1).coerceAtLeast(0L)
            } else {
                liked.add(userId)
                reel.likesCount + 1
            }
            transaction.update(docRef, "likedBy", liked)
            transaction.update(docRef, "likesCount", newCount)
        }.await()
    }

    // Economy & Games Profile
    suspend fun getOrCreateEconomyProfile(userId: String): com.example.data.model.EconomyProfile {
        val docRef = db.collection("economy_profiles").document(userId)
        val snap = docRef.get().await()
        if (snap.exists()) {
            val prof = snap.toObject(com.example.data.model.EconomyProfile::class.java)
            if (prof != null) return prof
        }
        val newProf = com.example.data.model.EconomyProfile(userId = userId)
        docRef.set(newProf).await()
        return newProf
    }

    suspend fun claimDailyReward(userId: String): com.example.data.model.EconomyProfile {
        val docRef = db.collection("economy_profiles").document(userId)
        val snap = docRef.get().await()
        val current = snap.toObject(com.example.data.model.EconomyProfile::class.java) ?: com.example.data.model.EconomyProfile(userId = userId)
        val updated = current.copy(
            coins = current.coins + 150L,
            gems = current.gems + 10L,
            xp = current.xp + 50L,
            dailyStreak = current.dailyStreak + 1,
            lastDailyRewardTimestamp = System.currentTimeMillis()
        )
        docRef.set(updated).await()
        return updated
    }

    // Anime Wiki
    fun observeAnimeWiki(): Flow<List<com.example.data.model.AnimeWikiItem>> {
        return db.collection("anime_wiki")
            .snapshots()
            .map { it.toObjects(com.example.data.model.AnimeWikiItem::class.java) }
    }

    // Reports (Admin & Moderation)
    suspend fun submitReport(report: com.example.data.model.ReportItem) {
        val docRef = db.collection("reports").document()
        docRef.set(report.copy(id = docRef.id)).await()
    }

    fun observeReports(): Flow<List<com.example.data.model.ReportItem>> {
        return db.collection("reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(com.example.data.model.ReportItem::class.java) }
    }

    // Seed default content
    suspend fun seedInitialContentIfEmpty(currentUser: FirebaseUser) {
        try {
            val postsSnapshot = db.collection("posts").limit(1).get().await()
            if (postsSnapshot.isEmpty) {
                Log.d(TAG, "Seeding initial anime posts and channels...")

                // Create initial channels with series & types
                val initialChannels = listOf(
                    Channel(
                        id = "channel_shonen",
                        title = "🔥 مجلس الشونين الأسطوري",
                        description = "نقاشات حامية حول ون بيس، جوجوتسو كايسن، دراغون بول وهجوم العمالقة",
                        animeCategory = "مجموعة عامة",
                        animeSeries = "شونين عام",
                        isBroadcastOnly = false,
                        isPrivate = false,
                        memberCount = 1240L,
                        admins = listOf(currentUser.uid),
                        createdBy = currentUser.uid,
                        lastMessageText = "من يتفق أن آرك الشيبويا هو الأفضل؟ 🔥",
                        lastMessageTime = System.currentTimeMillis() - 100000
                    ),
                    Channel(
                        id = "channel_news_broadcast",
                        title = "⚡ قناة أخبار وتسريبات الأنمي الرسمية",
                        description = "قناة بث رسمية لمواعيد صدور المواسم القادمة وإعلانات الاستوديوهات",
                        animeCategory = "قناة بث",
                        animeSeries = "أخبار الأنمي",
                        isBroadcastOnly = true,
                        isPrivate = false,
                        memberCount = 4520L,
                        admins = listOf(currentUser.uid),
                        createdBy = currentUser.uid,
                        lastMessageText = "عاجل: الإعلان عن موعد الموسم الجديد رسمياً!",
                        lastMessageTime = System.currentTimeMillis() - 50000
                    ),
                    Channel(
                        id = "channel_aot",
                        title = "⚔️ فيلق استطلاع هجوم العمالقة",
                        description = "غرفة محادثة خاصة بمحبي سلسلة Attack on Titan والتحليلات العميقة",
                        animeCategory = "سلسلة أنمي",
                        animeSeries = "هجوم العمالقة",
                        isBroadcastOnly = false,
                        isPrivate = false,
                        memberCount = 2890L,
                        admins = listOf(currentUser.uid),
                        createdBy = currentUser.uid,
                        lastMessageText = "ما رأيكم في ختام القصة ونهاية إرين؟",
                        lastMessageTime = System.currentTimeMillis() - 200000
                    ),
                    Channel(
                        id = "channel_onepiece",
                        title = "🍖 طاقم قبعة القش (ون بيس)",
                        description = "مناقشة فصول المانجا الأسبوعية وسر القرن الغائب والـ One Piece",
                        animeCategory = "سلسلة أنمي",
                        animeSeries = "ون بيس",
                        isBroadcastOnly = false,
                        isPrivate = false,
                        memberCount = 3710L,
                        admins = listOf(currentUser.uid),
                        createdBy = currentUser.uid,
                        lastMessageText = "من ينتظر ظهور شانكس القادم؟",
                        lastMessageTime = System.currentTimeMillis() - 400000
                    )
                )

                for (ch in initialChannels) {
                    db.collection("channels").document(ch.id).set(ch).await()
                    db.collection("channels").document(ch.id).collection("messages").document().set(
                        Message(
                            channelId = ch.id,
                            senderId = currentUser.uid,
                            senderName = currentUser.displayName ?: "إدارة بلاك انمي",
                            senderAvatarUrl = currentUser.photoUrl?.toString() ?: "",
                            content = ch.lastMessageText,
                            timestamp = ch.lastMessageTime
                        )
                    ).await()
                }

                // Initial posts (including news, groups, and character posts)
                val initialPosts = listOf(
                    Post(
                        id = "post_1",
                        authorId = currentUser.uid,
                        authorName = "ليفاي أكرمان",
                        authorRole = "قائد فيلق الاستطلاع",
                        content = "الخيار الوحيد هو المضي قدماً بلا ندم. شاركوني ما هي أفضل مقولة أثرت فيكم في عالم الأنمي؟ ⚔️🔥",
                        animeTitle = "هجوم العمالقة",
                        tags = listOf("هجوم_العمالقة", "شونين", "اقتباسات", "أساطير"),
                        likesCount = 54L,
                        sharesCount = 12L,
                        mediaUrl = "character",
                        mediaType = "IMAGE",
                        createdAt = System.currentTimeMillis() - 3600000
                    ),
                    Post(
                        id = "post_2",
                        authorId = "news_bot",
                        authorName = "أخبار الأنمي الرسمية",
                        authorRole = "موثق ⚡",
                        content = "رسمياً: استوديو مابا يعلن عن إنتاج موسم جديد ومميز قادم في خريف 2026 مع تحسينات بصرية ضخمة! 🍿✨",
                        animeTitle = "جوجوتسو كايسن",
                        tags = listOf("أخبار", "مابا", "جوجوتسو_كايسن"),
                        likesCount = 112L,
                        sharesCount = 38L,
                        mediaUrl = "banner",
                        mediaType = "NEWS",
                        isNews = true,
                        createdAt = System.currentTimeMillis() - 7200000
                    ),
                    Post(
                        id = "post_3",
                        authorId = currentUser.uid,
                        authorName = "سون غوكو",
                        authorRole = "محارب السايان",
                        content = "لا يوجد سقف للقوة عندما تتدرب بقلب نقي لحماية أصدقائك! من متحمس لأقوى قتالات الموسم؟ 💥👊",
                        animeTitle = "دراغون بول",
                        tags = listOf("دراغون_بول", "غوكو", "قتالات"),
                        likesCount = 85L,
                        sharesCount = 20L,
                        mediaUrl = "battle",
                        mediaType = "IMAGE",
                        createdAt = System.currentTimeMillis() - 10800000
                    )
                )

                for (p in initialPosts) {
                    db.collection("posts").document(p.id).set(p).await()
                }

                // Initial Live Stream sample
                db.collection("live_streams").document("stream_sample_1").set(
                    LiveStream(
                        id = "stream_sample_1",
                        hostId = currentUser.uid,
                        hostName = currentUser.displayName ?: "قائد الأوتاكو",
                        hostAvatarUrl = currentUser.photoUrl?.toString() ?: "",
                        title = "🔴 مناقشة حية: تحليل أهم أحداث فصول ون بيس وجوجوتسو!",
                        animeTopic = "ون بيس وجوجوتسو كايسن",
                        viewersCount = 142L,
                        isLive = true,
                        previewImage = "banner",
                        startedAt = System.currentTimeMillis()
                    )
                ).await()

                // Initial Reels
                val initialReels = listOf(
                    com.example.data.model.ReelItem(
                        id = "reel_1",
                        authorId = currentUser.uid,
                        authorName = "ليفاي أكرمان",
                        authorAvatarUrl = currentUser.photoUrl?.toString() ?: "",
                        videoPreviewRes = "character",
                        caption = "اللقطة الأسطورية ضد العملاق القرد بلا رحمة! ⚔️🔥",
                        animeTitle = "هجوم العمالقة",
                        likesCount = 1240L,
                        commentsCount = 89L,
                        sharesCount = 310L
                    ),
                    com.example.data.model.ReelItem(
                        id = "reel_2",
                        authorId = currentUser.uid,
                        authorName = "غوجو ساتورو",
                        authorAvatarUrl = currentUser.photoUrl?.toString() ?: "",
                        videoPreviewRes = "battle",
                        caption = "تفعيل تقنية الفراغ اللانهائي في ذروة القتال! ✨👁️",
                        animeTitle = "جوجوتسو كايسن",
                        likesCount = 2890L,
                        commentsCount = 210L,
                        sharesCount = 540L
                    )
                )
                for (r in initialReels) {
                    db.collection("reels").document(r.id).set(r).await()
                }

                // Welcome notification
                db.collection("notifications").document().set(
                    NotificationItem(
                        recipientId = currentUser.uid,
                        senderId = "system_blackanime",
                        senderName = "بلاك انمي (الإدارة)",
                        title = "أهلاً بك في بلاك انمي! 🖤🔥",
                        message = "مرحباً بك في أضخم مجتمع عربي للأنمي والمانجا. استكشف القنوات، والبثوث المباشرة وشارك منشوراتك الآن!",
                        type = "ANNOUNCEMENT",
                        isRead = false,
                        createdAt = System.currentTimeMillis()
                    )
                ).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Seeding error: ${e.message}", e)
        }
    }

    // ==================== ANIMES MANAGEMENT ====================

    fun observeAnimes(): Flow<List<AnimeItem>> {
        return db.collection("animes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(AnimeItem::class.java) }
            }
    }

    suspend fun getAnimeById(animeId: String): AnimeItem? {
        val doc = db.collection("animes").document(animeId).get().await()
        return if (doc.exists()) doc.toObject(AnimeItem::class.java) else null
    }

    suspend fun addOrUpdateAnime(anime: AnimeItem, adminUser: FirebaseUser): Result<Unit> {
        return try {
            val cleanId = anime.id.trim()
            if (cleanId.isBlank()) {
                return Result.failure(IllegalArgumentException("معرف الأنمي (Anime ID) مطلوب ولا يمكن تركه فارغاً"))
            }
            if (anime.titleArabic.isBlank()) {
                return Result.failure(IllegalArgumentException("عنوان الأنمي بالعربية مطلوب"))
            }

            val toSave = anime.copy(
                id = cleanId,
                addedBy = adminUser.uid,
                addedByEmail = adminUser.email ?: ""
            )

            db.collection("animes").document(cleanId).set(toSave).await()

            // Synchronize with anime_wiki collection for seamless app integration
            db.collection("anime_wiki").document(cleanId).set(
                com.example.data.model.AnimeWikiItem(
                    id = cleanId,
                    titleArabic = toSave.titleArabic,
                    titleRomaji = toSave.titleEnglish,
                    synopsis = toSave.synopsisArabic,
                    genres = toSave.genres,
                    episodesCount = toSave.episodesCount,
                    rating = toSave.rating,
                    status = toSave.status,
                    season = toSave.season,
                    coverImage = toSave.coverImageUrl
                )
            ).await()

            // Record in persistent audit logs
            logAdminAudit(
                AdminAuditLog(
                    id = "log_${System.currentTimeMillis()}",
                    adminId = adminUser.uid,
                    adminEmail = adminUser.email ?: "",
                    actionType = "ADD_OR_UPDATE_ANIME",
                    targetId = cleanId,
                    targetTitle = "${toSave.titleArabic} (${toSave.titleEnglish})",
                    details = "تم حفظ ونشر بيانات الأنمي بنجاح",
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding/updating anime: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAnime(animeId: String, animeTitle: String, adminUser: FirebaseUser): Result<Unit> {
        return try {
            db.collection("animes").document(animeId).delete().await()
            db.collection("anime_wiki").document(animeId).delete().await()

            logAdminAudit(
                AdminAuditLog(
                    id = "log_${System.currentTimeMillis()}",
                    adminId = adminUser.uid,
                    adminEmail = adminUser.email ?: "",
                    actionType = "DELETE_ANIME",
                    targetId = animeId,
                    targetTitle = animeTitle,
                    details = "تم حذف الأنمي من المنصة بواسطة المسؤول",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting anime: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==================== MODERATION & REPORTS ====================

    suspend fun resolveReport(reportId: String, actionTaken: String, adminUser: FirebaseUser): Result<Unit> {
        return try {
            db.collection("reports").document(reportId).update(
                mapOf(
                    "status" to "RESOLVED",
                    "actionTaken" to actionTaken,
                    "reviewedBy" to (adminUser.email ?: adminUser.uid)
                )
            ).await()

            logAdminAudit(
                AdminAuditLog(
                    id = "log_${System.currentTimeMillis()}",
                    adminId = adminUser.uid,
                    adminEmail = adminUser.email ?: "",
                    actionType = "RESOLVE_REPORT",
                    targetId = reportId,
                    targetTitle = "بلاغ رقم $reportId",
                    details = "تمت معالجة البلاغ: $actionTaken",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving report: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun setUserBanStatus(userId: String, isBanned: Boolean, reason: String, adminUser: FirebaseUser): Result<Unit> {
        return try {
            db.collection("users").document(userId).update(
                mapOf(
                    "isBanned" to isBanned,
                    "banReason" to reason
                )
            ).await()

            logAdminAudit(
                AdminAuditLog(
                    id = "log_${System.currentTimeMillis()}",
                    adminId = adminUser.uid,
                    adminEmail = adminUser.email ?: "",
                    actionType = if (isBanned) "BAN_USER" else "UNBAN_USER",
                    targetId = userId,
                    targetTitle = "المستخدم $userId",
                    details = if (isBanned) "تم حظر المستخدم. السبب: $reason" else "تم فك الحظر عن المستخدم",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user ban status: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteViolatingPost(postId: String, reason: String, adminUser: FirebaseUser): Result<Unit> {
        return try {
            db.collection("posts").document(postId).delete().await()

            logAdminAudit(
                AdminAuditLog(
                    id = "log_${System.currentTimeMillis()}",
                    adminId = adminUser.uid,
                    adminEmail = adminUser.email ?: "",
                    actionType = "DELETE_POST",
                    targetId = postId,
                    targetTitle = "منشور مخالف",
                    details = "تم حذف المنشور بواسطة الإدارة. السبب: $reason",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting post: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==================== AUDIT LOGS ====================

    fun observeAdminLogs(): Flow<List<AdminAuditLog>> {
        return db.collection("admin_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(AdminAuditLog::class.java) }
            }
    }

    private suspend fun logAdminAudit(log: AdminAuditLog) {
        try {
            db.collection("admin_logs").document(log.id).set(log).await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to write audit log: ${e.message}")
        }
    }
}
