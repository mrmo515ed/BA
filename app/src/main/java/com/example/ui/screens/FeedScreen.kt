package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.LiveStream
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.UserProfile
import com.example.ui.components.AddStoryButton
import com.example.ui.components.AnimeAvatar
import com.example.ui.components.AnimeStoryCircle
import com.example.ui.theme.AnimeBlackBg
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCardSurfaceHover
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeedScreen(
    posts: List<Post>,
    stories: List<Story>,
    liveStreams: List<LiveStream>,
    currentUserProfile: UserProfile?,
    currentUserId: String,
    onLikeClick: (String) -> Unit,
    onCommentClick: (Post) -> Unit,
    onReShareClick: (String) -> Unit,
    onStoryClick: (Story) -> Unit,
    onLiveStreamClick: (LiveStream) -> Unit,
    onStartLiveClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onCreatePostClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedFeedTabIndex by remember { mutableIntStateOf(0) }
    val feedTabs = listOf("الكل", "الأصدقاء والمتابعون", "أخبار الأنمي", "المجموعات")

    val friendsAndFollowing = currentUserProfile?.following.orEmpty() + currentUserProfile?.friends.orEmpty()

    val filteredPosts = remember(posts, selectedFeedTabIndex, friendsAndFollowing) {
        when (selectedFeedTabIndex) {
            1 -> posts.filter { friendsAndFollowing.contains(it.authorId) || it.authorId == currentUserId }
            2 -> posts.filter { it.isNews || it.mediaType == "NEWS" || it.animeTitle.contains("أخبار") || it.tags.contains("أخبار") }
            3 -> posts.filter { it.groupId.isNotEmpty() || it.authorRole.contains("كلان") || it.authorRole.contains("قائد") }
            else -> posts
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("feed_posts_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Live Streams Bar (if any or prompt to start)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AnimeCrimson)
                        )
                        Text(
                            text = "البثوث المباشرة للأنمي 🔴",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AnimeTextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeCrimson.copy(alpha = 0.15f))
                            .clickable { onStartLiveClick() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = AnimeCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "بدء بث مباشر",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnimeCrimson
                            )
                        }
                    }
                }

                if (liveStreams.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(liveStreams, key = { it.id }) { stream ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(100.dp)
                                    .clickable { onLiveStreamClick(stream) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                                border = BorderStroke(1.dp, AnimeCrimson)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.anime_manga_art_1791388998934),
                                        contentDescription = stream.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.65f))
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(AnimeCrimson)
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "🔴 مباشر",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "👁️ ${stream.viewersCount}",
                                                color = Color.White,
                                                fontSize = 10.sp
                                            )
                                        }

                                        Text(
                                            text = stream.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Stories Carousel
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "قصص وحالات الأوتاكو ⚡",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AnimeTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            AddStoryButton(onClick = onAddStoryClick)
                        }
                        items(stories, key = { it.id }) { story ->
                            AnimeStoryCircle(
                                story = story,
                                onClick = { onStoryClick(story) }
                            )
                        }
                    }
                }
            }

            // Feed Navigation Tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedFeedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = AnimeCrimson,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFeedTabIndex]),
                            color = AnimeCrimson
                        )
                    },
                    divider = {}
                ) {
                    feedTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedFeedTabIndex == index,
                            onClick = { selectedFeedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedFeedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedFeedTabIndex == index) AnimeCrimson else AnimeTextSecondary
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Posts list
            if (filteredPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⚔️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد منشورات في هذا القسم حالياً",
                                color = AnimeTextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "كن أول من ينشر أو تابع أصدقاء جدد!",
                                color = AnimeCrimson,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
                    AnimePostCard(
                        post = post,
                        currentUserId = currentUserId,
                        onLikeClick = { onLikeClick(post.id) },
                        onCommentClick = { onCommentClick(post) },
                        onReShareClick = {
                            onReShareClick(post.id)
                            Toast.makeText(context, "تمت إعادة مشاركة المنشور بنجاح! 🔁", Toast.LENGTH_SHORT).show()
                        },
                        onShareClick = {
                            Toast.makeText(context, "تم نسخ رابط المنشور! 🔥", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Floating Action Button to create post
        FloatingActionButton(
            onClick = onCreatePostClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .testTag("create_post_fab"),
            containerColor = AnimeCrimson,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "نشر منشور")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "انشر الآن",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AnimePostCard(
    post: Post,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onReShareClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val isLiked = post.likedBy.contains(currentUserId)
    var isSaved by remember { mutableStateOf(false) }

    val heartColor by animateColorAsState(
        targetValue = if (isLiked) AnimeCrimson else AnimeTextSecondary,
        animationSpec = spring(),
        label = "heartColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
        border = BorderStroke(1.dp, AnimeBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AnimeAvatar(
                        avatarUrl = post.authorAvatarUrl,
                        displayName = post.authorName,
                        size = 42
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AnimeTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeViolet.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = post.authorRole,
                                    fontSize = 9.sp,
                                    color = AnimeViolet,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = formatTimestamp(post.createdAt),
                            fontSize = 11.sp,
                            color = AnimeTextMuted
                        )
                    }
                }

                if (post.animeTitle.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeCardSurfaceHover)
                            .border(1.dp, AnimeBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚔️ ${post.animeTitle}",
                            fontSize = 11.sp,
                            color = AnimeGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content Text
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = AnimeTextPrimary,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
            )

            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    post.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            fontSize = 12.sp,
                            color = AnimeViolet,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Media
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AnimeBlackBg)
            ) {
                when (post.mediaUrl) {
                    "battle" -> {
                        Image(
                            painter = painterResource(id = R.drawable.anime_manga_art_1791388998934),
                            contentDescription = post.animeTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "banner" -> {
                        Image(
                            painter = painterResource(id = R.drawable.black_anime_banner_1791388840143),
                            contentDescription = post.animeTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "character" -> {
                        Image(
                            painter = painterResource(id = R.drawable.anime_character_art_1791388984389),
                            contentDescription = post.animeTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        if (post.mediaUrl.startsWith("http")) {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = post.animeTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val defaultRes = if (post.id.hashCode() % 2 == 0)
                                R.drawable.anime_character_art_1791388984389
                            else
                                R.drawable.anime_manga_art_1791388998934
                            Image(
                                painter = painterResource(id = defaultRes),
                                contentDescription = post.animeTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Like
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onLikeClick() }
                            .padding(vertical = 4.dp)
                            .testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "إعجاب",
                            tint = heartColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.likesCount.toString(),
                            fontSize = 13.sp,
                            color = if (isLiked) AnimeCrimson else AnimeTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Comments
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onCommentClick() }
                            .padding(vertical = 4.dp)
                            .testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "التعليقات",
                            tint = AnimeTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.commentsCount.toString(),
                            fontSize = 13.sp,
                            color = AnimeTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Re-share
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onReShareClick() }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "إعادة مشاركة",
                            tint = AnimeTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.sharesCount.toString(),
                            fontSize = 13.sp,
                            color = AnimeTextSecondary
                        )
                    }

                    // Share link
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = AnimeTextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                // Bookmark
                IconButton(
                    onClick = { isSaved = !isSaved },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "حفظ",
                        tint = if (isSaved) AnimeGold else AnimeTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timeMillis: Long): String {
    val diff = System.currentTimeMillis() - timeMillis
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "الآن"
        minutes < 60 -> "منذ $minutes دقيقة"
        hours < 24 -> "منذ $hours ساعة"
        days < 7 -> "منذ $days يوم"
        else -> SimpleDateFormat("d MMM", Locale("ar")).format(Date(timeMillis))
    }
}
