package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Channel
import com.example.data.model.FriendRequest
import com.example.data.model.LiveStream
import com.example.data.model.Post
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("بلاك انمي", appName)

        val firestoreDbId = context.getString(R.string.firestore_database_id)
        assertNotNull(firestoreDbId)
    }

    @Test
    fun `test user profile and post data models`() {
        val profile = UserProfile(
            userId = "user_123",
            username = "otaku_king",
            displayName = "ملك الأوتاكو",
            favoriteAnime = "هجوم العمالقة",
            following = listOf("user_456"),
            friends = listOf("user_789")
        )
        assertEquals("user_123", profile.userId)
        assertEquals("otaku_king", profile.username)
        assertTrue(profile.following.contains("user_456"))

        val post = Post(
            id = "post_1",
            authorId = profile.userId,
            authorName = profile.displayName,
            content = "أفضل حلقة في هجوم العمالقة!",
            animeTitle = "هجوم العمالقة",
            sharesCount = 5L
        )
        assertEquals("post_1", post.id)
        assertEquals(5L, post.sharesCount)

        val stream = LiveStream(
            id = "stream_1",
            hostId = profile.userId,
            title = "بث مباشر لأنمي ون بيس",
            isLive = true
        )
        assertTrue(stream.isLive)

        val friendRequest = FriendRequest(
            senderId = "user_123",
            receiverId = "user_456",
            status = "PENDING"
        )
        assertEquals("PENDING", friendRequest.status)

        val channel = Channel(
            title = "قناة ون بيس الرسمية",
            isBroadcastOnly = true
        )
        assertTrue(channel.isBroadcastOnly)
    }
}
