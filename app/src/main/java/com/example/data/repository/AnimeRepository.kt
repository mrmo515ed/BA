package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Channel
import com.example.data.model.Comment
import com.example.data.model.Message
import com.example.data.model.NotificationItem
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val TAG = "AnimeRepository"

class AnimeRepository(
    private val db: FirebaseFirestore
) {
    // Secondary constructor to resolve databaseId safely
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    // User Profile
    suspend fun getOrCreateUserProfile(user: FirebaseUser): UserProfile {
        val docRef = db.collection("users").document(user.uid)
        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            val profile = snapshot.toObject(UserProfile::class.java)
            if (profile != null) return profile
        }

        val cleanUsername = (user.email?.substringBefore("@") ?: "otaku_${user.uid.take(5)}")
        val newProfile = UserProfile(
            userId = user.uid,
            username = cleanUsername,
            displayName = user.displayName ?: "محارب الأنمي",
            avatarUrl = user.photoUrl?.toString() ?: "",
            bio = "أوتاكو ومتابع أنمي في بلاك انمي 🔥",
            favoriteAnime = "هجوم العمالقة",
            favoriteCharacter = "ليفاي أكرمان",
            role = "أوتاكو مميز",
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

    suspend fun updateUserProfile(profile: UserProfile) {
        db.collection("users").document(profile.userId)
            .set(profile)
            .await()
    }

    // Posts
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

        // Increment user post count
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

    // Channels / Anime Clubs
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

    suspend fun sendNotification(notification: NotificationItem) {
        val docRef = db.collection("notifications").document()
        docRef.set(notification.copy(id = docRef.id)).await()
    }

    // Seed default content if the database is newly created
    suspend fun seedInitialContentIfEmpty(currentUser: FirebaseUser) {
        try {
            val postsSnapshot = db.collection("posts").limit(1).get().await()
            if (postsSnapshot.isEmpty) {
                Log.d(TAG, "Seeding initial anime posts and channels...")

                // Create initial channels
                val initialChannels = listOf(
                    Channel(
                        id = "channel_shonen",
                        title = "🔥 مجلس الشونين الأسطوري",
                        description = "نقاشات حامية حول ون بيس، جوجوتسو كايسن، دراغون بول وهجوم العمالقة",
                        animeCategory = "شونين وقتالات",
                        memberCount = 1240L,
                        createdBy = currentUser.uid,
                        lastMessageText = "من يتفق أن آرك الشيبويا هو الأفضل؟ 🔥",
                        lastMessageTime = System.currentTimeMillis() - 100000
                    ),
                    Channel(
                        id = "channel_news",
                        title = "⚡ أخبار وتسريبات الأنمي",
                        description = "مواعيد صدور المواسم القادمة، إعلانات الاستوديوهات والمقاطع الدعائية",
                        animeCategory = "أخبار رسمية",
                        memberCount = 3450L,
                        createdBy = currentUser.uid,
                        lastMessageText = "رسمياً: الإعلان عن موعد الجزء القادم!",
                        lastMessageTime = System.currentTimeMillis() - 50000
                    ),
                    Channel(
                        id = "channel_manga",
                        title = "📖 ديوانية المانجاكا والنظريات",
                        description = "تحليلات فصول المانجا الأسبوعية وتوقعات الأحداث المستقبلية (تحذير حرق)",
                        animeCategory = "مانجا ونظريات",
                        memberCount = 890L,
                        createdBy = currentUser.uid,
                        lastMessageText = "الفصل القادم سيقلب كل الموازين!",
                        lastMessageTime = System.currentTimeMillis() - 300000
                    ),
                    Channel(
                        id = "channel_recommendations",
                        title = "🍿 ترشيحات وتوصيات الموسم",
                        description = "أفضل أعمال الموسم الجديد وأنيميات مظلومة تستحق المشاهدة",
                        animeCategory = "توصيات ومراجعات",
                        memberCount = 2100L,
                        createdBy = currentUser.uid,
                        lastMessageText = "ما هو أفضل أنمي شاهدته هذا الشهر؟",
                        lastMessageTime = System.currentTimeMillis() - 600000
                    )
                )

                for (ch in initialChannels) {
                    db.collection("channels").document(ch.id).set(ch).await()
                    // Add sample message
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

                // Create initial posts
                val initialPosts = listOf(
                    Post(
                        id = "post_1",
                        authorId = currentUser.uid,
                        authorName = "ليفاي أكرمان",
                        authorRole = "قائد فيلق الاستطلاع",
                        content = "الخيار الوحيد هو المضي قدماً بلا ندم. شاركوني ما هي أفضل مقولة أثرت فيكم في عالم الأنمي؟ ⚔️🔥",
                        animeTitle = "هجوم العمالقة",
                        tags = listOf("هجوم_العمالقة", "شونين", "اقتباسات", "أساطير"),
                        likesCount = 42L,
                        likedBy = emptyList(),
                        commentsCount = 8L,
                        mediaType = "IMAGE",
                        createdAt = System.currentTimeMillis() - 3600000
                    ),
                    Post(
                        id = "post_2",
                        authorId = currentUser.uid,
                        authorName = "غوجو ساتورو",
                        authorRole = "الساحر الأقوى",
                        content = "التوسع في المجال: الفراغ اللانهائي! 🔥✨ ما رأيكم في تحريك الملحمة الأخيرة؟ استوديو مابا تفوق على نفسه!",
                        animeTitle = "جوجوتسو كايسن",
                        tags = listOf("جوجوتسو_كايسن", "غوجو", "مابا", "قتالات"),
                        likesCount = 89L,
                        likedBy = emptyList(),
                        commentsCount = 15L,
                        mediaType = "IMAGE",
                        createdAt = System.currentTimeMillis() - 7200000
                    ),
                    Post(
                        id = "post_3",
                        authorId = currentUser.uid,
                        authorName = "مونكي دي لوفي",
                        authorRole = "ملك القراصنة القادم",
                        content = "لن أستسلم حتى أصل إلى قمة العالم مع طاقمي! من ينتظر حلقة هذا الأسبوع؟ 🍖🏴‍☠️",
                        animeTitle = "ون بيس",
                        tags = listOf("ون_بيس", "لوفي", "إييتشيرو_أودا", "مغامرات"),
                        likesCount = 135L,
                        likedBy = emptyList(),
                        commentsCount = 22L,
                        mediaType = "DISCUSSION",
                        createdAt = System.currentTimeMillis() - 10800000
                    )
                )

                for (p in initialPosts) {
                    db.collection("posts").document(p.id).set(p).await()
                }

                // Initial welcome notification
                db.collection("notifications").document().set(
                    NotificationItem(
                        recipientId = currentUser.uid,
                        senderId = "system_blackanime",
                        senderName = "بلاك انمي (الإدارة)",
                        title = "أهلاً بك في بلاك انمي! 🖤🔥",
                        message = "مرحباً بك في أضخم مجتمع عربي للأنمي والمانجا. استكشف القنوات وشارك منشوراتك الآن!",
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
}
