package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Post
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
            displayName = "ملك الأوتاكو"
        )
        assertEquals("user_123", profile.userId)
        assertEquals("otaku_king", profile.username)

        val post = Post(
            id = "post_1",
            authorId = profile.userId,
            authorName = profile.displayName,
            content = "أفضل حلقة في هجوم العمالقة!",
            animeTitle = "هجوم العمالقة"
        )
        assertEquals("post_1", post.id)
        assertEquals("هجوم العمالقة", post.animeTitle)
    }
}
