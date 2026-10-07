package com.example.domain.usecase

import com.example.data.model.Channel
import com.example.data.model.FriendRequest
import com.example.data.model.LiveComment
import com.example.data.model.LiveStream
import com.example.data.model.Message
import com.example.data.model.Post
import com.example.data.model.ReelItem
import com.example.data.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow

class GetFeedPostsUseCase(private val repository: AnimeRepository) {
    operator fun invoke(): Flow<List<Post>> = repository.observePosts()
}

class CreatePostUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(post: Post) = repository.createPost(post)
}

class ToggleLikeUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(postId: String, userId: String, userName: String = "") =
        repository.toggleLikePost(postId, userId, userName)
}

class GetChannelsUseCase(private val repository: AnimeRepository) {
    operator fun invoke(): Flow<List<Channel>> = repository.observeChannels()
}

class SendMessageUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(message: Message) = repository.sendMessage(message)
}

class LiveStreamUseCase(private val repository: AnimeRepository) {
    fun observeStreams(): Flow<List<LiveStream>> = repository.observeLiveStreams()
    suspend fun startStream(stream: LiveStream): String = repository.startLiveStream(stream)
    suspend fun updateControls(streamId: String, isMuted: Boolean, isCameraOff: Boolean) =
        repository.updateLiveControls(streamId, isMuted, isCameraOff)
    suspend fun endStream(streamId: String) = repository.endLiveStream(streamId)
    fun observeComments(streamId: String): Flow<List<LiveComment>> = repository.observeLiveComments(streamId)
    suspend fun sendComment(comment: LiveComment) = repository.sendLiveComment(comment)
}

class UserFriendshipUseCase(private val repository: AnimeRepository) {
    suspend fun follow(currentUserId: String, targetUserId: String) =
        repository.followUser(currentUserId, targetUserId)
    suspend fun unfollow(currentUserId: String, targetUserId: String) =
        repository.unfollowUser(currentUserId, targetUserId)
    suspend fun sendRequest(request: FriendRequest) =
        repository.sendFriendRequest(request)
    suspend fun respondRequest(requestId: String, accept: Boolean, senderId: String, receiverId: String) =
        repository.respondToFriendRequest(requestId, accept, senderId, receiverId)
}
