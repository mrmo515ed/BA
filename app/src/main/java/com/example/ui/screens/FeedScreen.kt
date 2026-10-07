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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Post
import com.example.data.model.Story
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
    currentUserId: String,
    onLikeClick: (String) -> Unit,
    onCommentClick: (Post) -> Unit,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit,
    onCreatePostClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("الكل") }
    val categories = listOf("الكل", "شونين", "مانجا", "اقتباسات", "أخبار", "نظريات")

    // Filter posts based on category
    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == "الكل") posts
        else posts.filter { post ->
            post.tags.any { it.contains(selectedCategory) } ||
            post.content.contains(selectedCategory) ||
            post.animeTitle.contains(selectedCategory)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("feed_posts_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Stories Carousel
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "قصص الأوتاكو ⚡",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AnimeTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
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

            // Categories Filter Chips
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) AnimeCrimson else AnimeCardSurface)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else AnimeTextSecondary
                            )
                        }
                    }
                }
            }

            // Empty state if no posts
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
                                text = "لا توجد منشورات في هذا القسم بعد",
                                color = AnimeTextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "كن أول من ينشر في بلاك انمي!",
                                color = AnimeCrimson,
                                fontSize = 13.sp
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
                        onShareClick = {
                            Toast.makeText(context, "تم نسخ رابط المنشور بنجاح! 🔥", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // FAB to create a post
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
            // Post Header
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
                            // Badge role
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

                // Anime Tag Pill
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

            // Post Content Text
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = AnimeTextPrimary,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
            )

            // Tags
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

            // Post Visual Artwork / Media
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
                            // Alternate default art based on post id
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

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Like button
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

                    // Comments button
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

                    // Share button
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

                // Bookmark / Save button
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
