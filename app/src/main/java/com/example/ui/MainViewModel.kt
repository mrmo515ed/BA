package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Channel
import com.example.data.model.Comment
import com.example.data.model.Message
import com.example.data.model.NotificationItem
import com.example.data.model.Post
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

    private val _searchFilter = MutableStateFlow("ALL") // ALL, USERS, POSTS, CHANNELS
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // Ensure profile is initialized in Firestore
                val profile = repository.getOrCreateUserProfile(currentUser)
                _userProfile.value = profile

                // Seed initial anime content if first run
                repository.seedInitialContentIfEmpty(currentUser)

                // Observe real-time user profile
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

    fun createChannel(title: String, description: String, category: String) {
        viewModelScope.launch {
            try {
                val channel = Channel(
                    title = title,
                    description = description,
                    animeCategory = category,
                    memberCount = 1L,
                    members = listOf(currentUserId),
                    createdBy = currentUserId,
                    lastMessageText = "تم إنشاء القناة بواسطة المنشئ",
                    lastMessageTime = System.currentTimeMillis()
                )
                val channelId = repository.createChannel(channel)
                // Open created channel immediately
                openChannel(channel.copy(id = channelId))
            } catch (e: Exception) {
                Log.e(TAG, "Create channel failed: ${e.message}", e)
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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(filter: String) {
        _searchFilter.value = filter
    }
}
