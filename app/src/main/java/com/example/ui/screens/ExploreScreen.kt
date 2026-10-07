package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Channel
import com.example.data.model.Post
import com.example.data.model.UserProfile
import com.example.ui.components.AnimeAvatar
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCardSurfaceHover
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    posts: List<Post>,
    channels: List<Channel>,
    currentUserProfile: UserProfile?,
    onPostClick: (Post) -> Unit,
    onChannelClick: (Channel) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("الكل", "المحتوى المرئي", "القنوات والنوادي", "الأوتاكو")

    val trendingTags = listOf(
        "هجوم_العمالقة" to "1.8K منشور",
        "جوجوتسو_كايسن" to "1.4K منشور",
        "ون_بيس" to "3.2K منشور",
        "سولو_ليفلينغ" to "920 منشور",
        "قاتل_الشياطين" to "1.1K منشور",
        "ديث_نوت" to "650 منشور"
    )

    // Filter results according to query
    val trimmedQuery = searchQuery.trim()

    val matchedPosts = remember(posts, trimmedQuery) {
        if (trimmedQuery.isEmpty()) emptyList()
        else posts.filter {
            it.content.contains(trimmedQuery, ignoreCase = true) ||
            it.animeTitle.contains(trimmedQuery, ignoreCase = true) ||
            it.authorName.contains(trimmedQuery, ignoreCase = true) ||
            it.tags.any { tag -> tag.contains(trimmedQuery, ignoreCase = true) }
        }
    }

    val matchedChannels = remember(channels, trimmedQuery) {
        if (trimmedQuery.isEmpty()) emptyList()
        else channels.filter {
            it.title.contains(trimmedQuery, ignoreCase = true) ||
            it.description.contains(trimmedQuery, ignoreCase = true) ||
            it.animeCategory.contains(trimmedQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        // Search Header Field
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "ابحث عن أنمي، منشورات، نوادي، أو مستخدمين...",
                        fontSize = 13.sp,
                        color = AnimeTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = AnimeCrimson
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "مسح",
                                tint = AnimeTextSecondary
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCrimson,
                    unfocusedBorderColor = AnimeBorder,
                    focusedTextColor = AnimeTextPrimary,
                    unfocusedTextColor = AnimeTextPrimary,
                    focusedContainerColor = AnimeCardSurface,
                    unfocusedContainerColor = AnimeCardSurface
                ),
                singleLine = true
            )
        }

        // Search Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = AnimeCrimson,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = AnimeCrimson
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) AnimeCrimson else AnimeTextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content Area
        if (searchQuery.isBlank()) {
            // Default Discovery Screen: Trending Hashtags + Visual Grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Trending Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "الترند",
                                tint = AnimeGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "الترند الحالي في بلاك انمي 🔥",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AnimeTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            trendingTags.forEach { (tag, count) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AnimeCardSurface)
                                        .border(1.dp, AnimeBorder, RoundedCornerShape(12.dp))
                                        .clickable { onSearchQueryChange(tag) }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "#$tag",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AnimeViolet
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = count,
                                            fontSize = 10.sp,
                                            color = AnimeTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Visual Explore Gallery Header
                item {
                    Text(
                        text = "استكشاف المحتوى المرئي والأعمال 🎨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AnimeTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                // Visual Grid
                item {
                    val visualArtItems = listOf(
                        Triple(R.drawable.anime_character_art_1791388984389, "سولو ليفلينغ", "🔥 890 إعجاب"),
                        Triple(R.drawable.anime_manga_art_1791388998934, "صدام العمالقة", "⚔️ 1.2K إعجاب"),
                        Triple(R.drawable.black_anime_banner_1791388840143, "طوكيو الليلية", "🌃 950 إعجاب"),
                        Triple(R.drawable.black_anime_logo_1791388824621, "رمز بلاك انمي", "🖤 3.4K إعجاب")
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in visualArtItems.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val item1 = visualArtItems[i]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onSearchQueryChange(item1.second) }
                                ) {
                                    Image(
                                        painter = painterResource(id = item1.first),
                                        contentDescription = item1.second,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .background(Color.Black.copy(alpha = 0.65f))
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${item1.second} • ${item1.third}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (i + 1 < visualArtItems.size) {
                                    val item2 = visualArtItems[i + 1]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { onSearchQueryChange(item2.second) }
                                    ) {
                                        Image(
                                            painter = painterResource(id = item2.first),
                                            contentDescription = item2.second,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(Color.Black.copy(alpha = 0.65f))
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "${item2.second} • ${item2.third}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Search Results Mode
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Channels results
                if (selectedTabIndex == 0 || selectedTabIndex == 2) {
                    if (matchedChannels.isNotEmpty()) {
                        item {
                            Text(
                                text = "النوادي والقنوات (${matchedChannels.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AnimeGold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(matchedChannels, key = { it.id }) { ch ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChannelClick(ch) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                                border = BorderStroke(1.dp, AnimeBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(AnimeViolet.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Groups,
                                            contentDescription = null,
                                            tint = AnimeViolet
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ch.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AnimeTextPrimary
                                        )
                                        Text(
                                            text = "${ch.animeCategory} • ${ch.memberCount} عضو",
                                            fontSize = 11.sp,
                                            color = AnimeTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Posts results
                if (selectedTabIndex == 0 || selectedTabIndex == 1) {
                    if (matchedPosts.isNotEmpty()) {
                        item {
                            Text(
                                text = "المنشورات والمحتوى المرئي (${matchedPosts.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AnimeCrimson,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(matchedPosts, key = { it.id }) { post ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPostClick(post) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                                border = BorderStroke(1.dp, AnimeBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    AnimeAvatar(
                                        avatarUrl = post.authorAvatarUrl,
                                        displayName = post.authorName,
                                        size = 38
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = post.authorName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = AnimeTextPrimary
                                            )
                                            Text(
                                                text = "⚔️ ${post.animeTitle}",
                                                fontSize = 11.sp,
                                                color = AnimeGold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = post.content,
                                            fontSize = 13.sp,
                                            color = AnimeTextSecondary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // If no results matched
                if (matchedPosts.isEmpty() && matchedChannels.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🔍", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "لم يتم العثور على نتائج لـ \"$trimmedQuery\"",
                                    color = AnimeTextSecondary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "جرب البحث باسم أنمي آخر أو وسم مختلف",
                                    color = AnimeTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
