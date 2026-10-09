package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.AnimeRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(
    private val repository: AnimeRepository,
    val currentUserId: String,
    val currentUser: FirebaseUser
) : ViewModel() {

    // User profile state
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // All Users
    val allUsers: StateFlow<List<UserProfile>> = repository.observeAllUsers()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Friend Requests
    val friendRequests: StateFlow<List<FriendRequest>> = repository.observeFriendRequests(currentUserId)
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Posts stream
    val posts: StateFlow<List<Post>> = repository.observePosts()
        .catch { e ->
            Log.e(TAG, "Error observing posts: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stories stream
    val stories: StateFlow<List<Story>> = repository.observeStories()
        .catch { e ->
            Log.e(TAG, "Error observing stories: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Channels stream
    val channels: StateFlow<List<Channel>> = repository.observeChannels()
        .catch { e ->
            Log.e(TAG, "Error observing channels: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Streams stream
    val liveStreams: StateFlow<List<LiveStream>> = repository.observeLiveStreams()
        .catch { e ->
            Log.e(TAG, "Error observing live streams: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Live Stream
    private val _activeLiveStream = MutableStateFlow<LiveStream?>(null)
    val activeLiveStream: StateFlow<LiveStream?> = _activeLiveStream.asStateFlow()

    private val _liveComments = MutableStateFlow<List<LiveComment>>(emptyList())
    val liveComments: StateFlow<List<LiveComment>> = _liveComments.asStateFlow()

    // Notifications stream
    val notifications: StateFlow<List<NotificationItem>> = repository.observeNotifications(currentUserId)
        .catch { e ->
            Log.e(TAG, "Error observing notifications: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Active Channel Chat Messages
    private val _activeChannel = MutableStateFlow<Channel?>(null)
    val activeChannel: StateFlow<Channel?> = _activeChannel.asStateFlow()

    private val _channelMessages = MutableStateFlow<List<Message>>(emptyList())
    val channelMessages: StateFlow<List<Message>> = _channelMessages.asStateFlow()

    // Active Post Comments
    private val _activePost = MutableStateFlow<Post?>(null)
    val activePost: StateFlow<Post?> = _activePost.asStateFlow()

    private val _postComments = MutableStateFlow<List<Comment>>(emptyList())
    val postComments: StateFlow<List<Comment>> = _postComments.asStateFlow()

    // Search query & filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow("ALL")
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val profile = repository.getOrCreateUserProfile(currentUser)
                _userProfile.value = profile

                repository.seedInitialContentIfEmpty(currentUser)

                repository.observeUserProfile(currentUserId).collect { updated ->
                    if (updated != null) {
                        _userProfile.value = updated
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Init user profile failed: ${e.message}", e)
            }
        }
    }

    // Live Streaming
    fun startLiveStream(title: String, topic: String, preview: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val stream = LiveStream(
                    hostId = currentUserId,
                    hostName = profile?.displayName ?: currentUser.displayName ?: "مضيف البث",
                    hostAvatarUrl = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: "",
                    title = title,
                    animeTopic = topic,
                    previewImage = preview,
                    viewersCount = 1L,
                    isLive = true
                )
                val streamId = repository.startLiveStream(stream)
                openLiveStream(stream.copy(id = streamId))
            } catch (e: Exception) {
                Log.e(TAG, "Start live stream failed: ${e.message}", e)
            }
        }
    }

    fun openLiveStream(stream: LiveStream) {
        _activeLiveStream.value = stream
        viewModelScope.launch {
            repository.observeLiveComments(stream.id).collect { comments ->
                _liveComments.value = comments
            }
        }
    }

    fun closeLiveStream() {
        val stream = _activeLiveStream.value
        if (stream != null && stream.hostId == currentUserId) {
            viewModelScope.launch {
                repository.endLiveStream(stream.id)
            }
        }
        _activeLiveStream.value = null
        _liveComments.value = emptyList()
    }

    fun toggleLiveControls(isMuted: Boolean, isCameraOff: Boolean) {
        val stream = _activeLiveStream.value ?: return
        _activeLiveStream.value = stream.copy(isMuted = isMuted, isCameraOff = isCameraOff)
        viewModelScope.launch {
            repository.updateLiveControls(stream.id, isMuted, isCameraOff)
        }
    }

    fun sendLiveComment(content: String) {
        val stream = _activeLiveStream.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            val profile = _userProfile.value
            val comment = LiveComment(
                streamId = stream.id,
                senderId = currentUserId,
                senderName = profile?.displayName ?: currentUser.displayName ?: "مشاهد",
                senderAvatarUrl = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: "",
                content = content.trim()
            )
            repository.sendLiveComment(comment)
        }
    }

    // Follow & Friendship
    fun toggleFollowUser(targetUserId: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value ?: return@launch
                if (profile.following.contains(targetUserId)) {
                    repository.unfollowUser(currentUserId, targetUserId)
                } else {
                    repository.followUser(currentUserId, targetUserId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Toggle follow failed: ${e.message}", e)
            }
        }
    }

    fun sendFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val request = FriendRequest(
                    senderId = currentUserId,
                    senderName = profile?.displayName ?: currentUser.displayName ?: "أوتاكو",
                    senderAvatarUrl = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: "",
                    receiverId = targetUserId,
                    status = "PENDING"
                )
                repository.sendFriendRequest(request)
            } catch (e: Exception) {
                Log.e(TAG, "Send friend request failed: ${e.message}", e)
            }
        }
    }

    fun respondToFriendRequest(requestId: String, accept: Boolean, senderId: String) {
        viewModelScope.launch {
            try {
                repository.respondToFriendRequest(requestId, accept, senderId, currentUserId)
            } catch (e: Exception) {
                Log.e(TAG, "Respond friend request failed: ${e.message}", e)
            }
        }
    }

    fun openChannel(channel: Channel) {
        _activeChannel.value = channel
        viewModelScope.launch {
            repository.observeMessages(channel.id).collect { msgs ->
                _channelMessages.value = msgs
            }
        }
    }

    fun closeChannel() {
        _activeChannel.value = null
        _channelMessages.value = emptyList()
    }

    fun openComments(post: Post) {
        _activePost.value = post
        viewModelScope.launch {
            repository.observeComments(post.id).collect { comments ->
                _postComments.value = comments
            }
        }
    }

    fun closeComments() {
        _activePost.value = null
        _postComments.value = emptyList()
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val userName = profile?.displayName ?: currentUser.displayName ?: "مستخدم"
                repository.toggleLikePost(postId, currentUserId, userName)
            } catch (e: Exception) {
                Log.e(TAG, "Toggle like failed: ${e.message}", e)
            }
        }
    }

    fun reSharePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.reSharePost(postId)
            } catch (e: Exception) {
                Log.e(TAG, "ReShare post failed: ${e.message}", e)
            }
        }
    }

    fun createPost(
        content: String,
        animeTitle: String,
        mediaUrl: String,
        mediaType: String,
        tags: List<String>
    ) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val authorName = profile?.displayName ?: currentUser.displayName ?: "أوتاكو"
                val authorAvatar = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: ""
                val authorRole = profile?.role ?: "أوتاكو مميز"

                val post = Post(
                    authorId = currentUserId,
                    authorName = authorName,
                    authorAvatarUrl = authorAvatar,
                    authorRole = authorRole,
                    content = content,
                    animeTitle = animeTitle,
                    mediaUrl = mediaUrl,
                    mediaType = mediaType,
                    tags = tags,
                    createdAt = System.currentTimeMillis()
                )
                repository.createPost(post)
            } catch (e: Exception) {
                Log.e(TAG, "Create post failed: ${e.message}", e)
            }
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val authorName = profile?.displayName ?: currentUser.displayName ?: "أوتاكو"
                val authorAvatar = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: ""

                val comment = Comment(
                    postId = postId,
                    authorId = currentUserId,
                    authorName = authorName,
                    authorAvatarUrl = authorAvatar,
                    content = content,
                    createdAt = System.currentTimeMillis()
                )
                repository.addComment(comment)
            } catch (e: Exception) {
                Log.e(TAG, "Add comment failed: ${e.message}", e)
            }
        }
    }

    fun createStory(imageUrl: String, caption: String, animeTag: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val authorName = profile?.displayName ?: currentUser.displayName ?: "أوتاكو"
                val authorAvatar = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: ""

                val story = Story(
                    authorId = currentUserId,
                    authorName = authorName,
                    authorAvatarUrl = authorAvatar,
                    imageUrl = imageUrl,
                    caption = caption,
                    animeTag = animeTag,
                    createdAt = System.currentTimeMillis()
                )
                repository.createStory(story)
            } catch (e: Exception) {
                Log.e(TAG, "Create story failed: ${e.message}", e)
            }
        }
    }

    fun createBroadcastOrGroup(
        title: String,
        description: String,
        category: String,
        series: String,
        isBroadcast: Boolean,
        isPrivate: Boolean
    ) {
        viewModelScope.launch {
            try {
                val channel = Channel(
                    title = title,
                    description = description,
                    animeCategory = category,
                    animeSeries = series,
                    isBroadcastOnly = isBroadcast,
                    isPrivate = isPrivate,
                    memberCount = 1L,
                    members = listOf(currentUserId),
                    admins = listOf(currentUserId),
                    createdBy = currentUserId,
                    lastMessageText = if (isBroadcast) "بدأ البث في القناة" else "تم إنشاء المجموعة",
                    lastMessageTime = System.currentTimeMillis()
                )
                val channelId = repository.createChannel(channel)
                openChannel(channel.copy(id = channelId))
            } catch (e: Exception) {
                Log.e(TAG, "Create channel/group failed: ${e.message}", e)
            }
        }
    }

    fun sendMessage(channelId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val senderName = profile?.displayName ?: currentUser.displayName ?: "أوتاكو"
                val senderAvatar = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: ""

                val message = Message(
                    channelId = channelId,
                    senderId = currentUserId,
                    senderName = senderName,
                    senderAvatarUrl = senderAvatar,
                    content = content.trim(),
                    timestamp = System.currentTimeMillis()
                )
                repository.sendMessage(message)
            } catch (e: Exception) {
                Log.e(TAG, "Send message failed: ${e.message}", e)
            }
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationAsRead(id)
            } catch (e: Exception) {
                Log.e(TAG, "Mark notification read failed: ${e.message}", e)
            }
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            try {
                repository.markAllNotificationsAsRead(currentUserId)
            } catch (e: Exception) {
                Log.e(TAG, "Mark all notifications read failed: ${e.message}", e)
            }
        }
    }

    fun updateProfile(
        displayName: String,
        bio: String,
        favoriteAnime: String,
        favoriteCharacter: String,
        role: String
    ) {
        viewModelScope.launch {
            try {
                val current = _userProfile.value ?: return@launch
                val updated = current.copy(
                    displayName = displayName.ifBlank { current.displayName },
                    bio = bio.ifBlank { current.bio },
                    favoriteAnime = favoriteAnime.ifBlank { current.favoriteAnime },
                    favoriteCharacter = favoriteCharacter.ifBlank { current.favoriteCharacter },
                    role = role.ifBlank { current.role }
                )
                repository.updateUserProfile(updated)
                _userProfile.value = updated
            } catch (e: Exception) {
                Log.e(TAG, "Update profile failed: ${e.message}", e)
            }
        }
    }

    // Reels stream
    val reels: StateFlow<List<com.example.data.model.ReelItem>> = repository.observeReels()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Economy profile
    private val _economyProfile = MutableStateFlow(com.example.data.model.EconomyProfile(userId = currentUserId))
    val economyProfile: StateFlow<com.example.data.model.EconomyProfile> = _economyProfile.asStateFlow()

    fun toggleLikeReel(reelId: String) {
        viewModelScope.launch {
            try {
                repository.toggleLikeReel(reelId, currentUserId)
            } catch (e: Exception) {
                Log.e(TAG, "Toggle like reel failed: ${e.message}", e)
            }
        }
    }

    fun createReel(caption: String, animeTitle: String, previewRes: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val reel = com.example.data.model.ReelItem(
                    authorId = currentUserId,
                    authorName = profile?.displayName ?: currentUser.displayName ?: "صانع ريلز",
                    authorAvatarUrl = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: "",
                    videoPreviewRes = previewRes,
                    caption = caption,
                    animeTitle = animeTitle
                )
                repository.createReel(reel)
            } catch (e: Exception) {
                Log.e(TAG, "Create reel failed: ${e.message}", e)
            }
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            try {
                val updated = repository.claimDailyReward(currentUserId)
                _economyProfile.value = updated
            } catch (e: Exception) {
                Log.e(TAG, "Claim daily reward failed: ${e.message}", e)
            }
        }
    }

    fun submitReport(targetId: String, targetType: String, reason: String) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val report = com.example.data.model.ReportItem(
                    reporterId = currentUserId,
                    reporterName = profile?.displayName ?: currentUser.displayName ?: "مستخدم",
                    targetId = targetId,
                    targetType = targetType,
                    reason = reason
                )
                repository.submitReport(report)
            } catch (e: Exception) {
                Log.e(TAG, "Submit report failed: ${e.message}", e)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(filter: String) {
        _searchFilter.value = filter
    }

    // ==================== ANIMES STREAM & OPS ====================

    val animes: StateFlow<List<AnimeItem>> = repository.observeAnimes()
        .catch { e ->
            Log.e(TAG, "Error observing animes: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==================== ADMIN & MODERATION ====================

    val reports: StateFlow<List<ReportItem>> = repository.observeReports()
        .catch { e ->
            Log.e(TAG, "Error observing reports: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminLogs: StateFlow<List<AdminAuditLog>> = repository.observeAdminLogs()
        .catch { e ->
            Log.e(TAG, "Error observing admin logs: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateAnime(anime: AnimeItem, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.addOrUpdateAnime(anime, currentUser)
            result.onSuccess {
                onResult(true, "تم حفظ ونشر بيانات الأنمي بنجاح في قاعدة البيانات 🖤✨")
            }.onFailure { err ->
                onResult(false, err.message ?: "حدث خطأ أثناء حفظ بيانات الأنمي")
            }
        }
    }

    fun deleteAnime(animeId: String, animeTitle: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteAnime(animeId, animeTitle, currentUser)
            result.onSuccess {
                onResult(true, "تم حذف الأنمي بنجاح")
            }.onFailure { err ->
                onResult(false, err.message ?: "فشل حذف الأنمي")
            }
        }
    }

    fun resolveReport(reportId: String, actionTaken: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.resolveReport(reportId, actionTaken, currentUser)
            result.onSuccess {
                onResult(true, "تمت معالجة البلاغ بنجاح")
            }.onFailure { err ->
                onResult(false, err.message ?: "فشل معالجة البلاغ")
            }
        }
    }

    fun setUserBanStatus(userId: String, isBanned: Boolean, reason: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.setUserBanStatus(userId, isBanned, reason, currentUser)
            result.onSuccess {
                onResult(true, if (isBanned) "تم حظر المستخدم بنجاح" else "تم إلغاء الحظر بنجاح")
            }.onFailure { err ->
                onResult(false, err.message ?: "فشل تحديث حالة الحظر")
            }
        }
    }

    fun deleteViolatingPost(postId: String, reason: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteViolatingPost(postId, reason, currentUser)
            result.onSuccess {
                onResult(true, "تم حذف المنشور المخالف بنجاح")
            }.onFailure { err ->
                onResult(false, err.message ?: "فشل حذف المنشور")
            }
        }
    }

    // ==================== USER ANIME TRACKING (Animesta & Kunaiu) ====================

    val userAnimeTracking: StateFlow<List<com.example.data.model.UserAnimeTracking>> = repository.observeUserAnimeTracking(currentUserId)
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAnimeTracking(
        animeId: String,
        titleAr: String,
        titleEn: String,
        cover: String,
        status: String,
        currentEpisode: Int,
        totalEpisodes: Int,
        score: Double,
        note: String
    ) {
        viewModelScope.launch {
            try {
                val tracking = com.example.data.model.UserAnimeTracking(
                    animeId = animeId,
                    userId = currentUserId,
                    animeTitleArabic = titleAr,
                    animeTitleEnglish = titleEn,
                    coverImage = cover,
                    status = status,
                    currentEpisode = currentEpisode,
                    totalEpisodes = totalEpisodes,
                    userScore = score,
                    note = note,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveOrUpdateAnimeTracking(currentUserId, tracking)
            } catch (e: Exception) {
                Log.e(TAG, "Update anime tracking failed: ${e.message}", e)
            }
        }
    }

    fun removeAnimeTracking(animeId: String) {
        viewModelScope.launch {
            try {
                repository.removeAnimeTracking(currentUserId, animeId)
            } catch (e: Exception) {
                Log.e(TAG, "Remove anime tracking failed: ${e.message}", e)
            }
        }
    }

    // ==================== ADVANCED CHAT & FEED (Telegram / WhatsApp / Instagram) ====================

    fun reactToMessage(channelId: String, messageId: String, emoji: String) {
        viewModelScope.launch {
            try {
                repository.reactToMessage(channelId, messageId, emoji, currentUserId)
            } catch (e: Exception) {
                Log.e(TAG, "React to message failed: ${e.message}", e)
            }
        }
    }

    fun voteInPoll(postId: String, optionIndex: Int) {
        viewModelScope.launch {
            try {
                repository.voteInPoll(postId, optionIndex, currentUserId)
            } catch (e: Exception) {
                Log.e(TAG, "Vote in poll failed: ${e.message}", e)
            }
        }
    }

    fun sendMessageAdvanced(
        channelId: String,
        content: String,
        mediaUrl: String = "",
        messageType: String = "TEXT",
        replyToMessageId: String = "",
        replyToSenderName: String = "",
        replyToContent: String = ""
    ) {
        if (content.isBlank() && mediaUrl.isBlank()) return
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val senderName = profile?.displayName ?: currentUser.displayName ?: "أوتاكو"
                val senderAvatar = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: ""

                val message = Message(
                    channelId = channelId,
                    senderId = currentUserId,
                    senderName = senderName,
                    senderAvatarUrl = senderAvatar,
                    content = content.trim(),
                    mediaUrl = mediaUrl,
                    messageType = messageType,
                    replyToMessageId = replyToMessageId,
                    replyToSenderName = replyToSenderName,
                    replyToContent = replyToContent,
                    timestamp = System.currentTimeMillis()
                )
                repository.sendMessage(message)
            } catch (e: Exception) {
                Log.e(TAG, "Send message advanced failed: ${e.message}", e)
            }
        }
    }

    fun createPostAdvanced(
        content: String,
        animeTitle: String,
        mediaUrl: String,
        mediaType: String,
        tags: List<String>,
        isSpoiler: Boolean = false,
        pollQuestion: String = "",
        pollOptions: List<String> = emptyList(),
        ratingScore: Double = 0.0
    ) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                val authorName = profile?.displayName ?: currentUser.displayName ?: "أوتاكو"
                val authorAvatar = profile?.avatarUrl ?: currentUser.photoUrl?.toString() ?: ""
                val authorRole = profile?.role ?: "أوتاكو مميز"

                val post = Post(
                    authorId = currentUserId,
                    authorName = authorName,
                    authorAvatarUrl = authorAvatar,
                    authorRole = authorRole,
                    content = content,
                    animeTitle = animeTitle,
                    mediaUrl = mediaUrl,
                    mediaType = mediaType,
                    tags = tags,
                    isSpoiler = isSpoiler,
                    pollQuestion = pollQuestion,
                    pollOptions = pollOptions,
                    ratingScore = ratingScore,
                    createdAt = System.currentTimeMillis()
                )
                repository.createPost(post)
            } catch (e: Exception) {
                Log.e(TAG, "Create post advanced failed: ${e.message}", e)
            }
        }
    }
}
