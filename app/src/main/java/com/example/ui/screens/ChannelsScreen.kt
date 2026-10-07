package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCardSurfaceHover
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet

@Composable
fun ChannelsScreen(
    channels: List<Channel>,
    currentUserId: String,
    onChannelClick: (Channel) -> Unit,
    onCreateChannelClick: () -> Unit
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("الكل", "غرف سلاسل الأنمي", "قنوات البث", "مجموعات النقاش")

    val filteredChannels = remember(channels, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> channels.filter { it.animeSeries != "عام" || it.animeCategory.contains("سلسلة") }
            2 -> channels.filter { it.isBroadcastOnly }
            3 -> channels.filter { !it.isBroadcastOnly }
            else -> channels
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                    Text(
                        text = "نوادي، قنوات وغرف الأنمي 🛡️",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = AnimeTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "انضم إلى غرف سلاسل الأنمي، قنوات البث الرسمية، أو أسس مجموعتك الخاصة",
                        fontSize = 12.sp,
                        color = AnimeTextSecondary
                    )
                }
            }

            // Filters TabRow
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedFilterIndex,
                    containerColor = Color.Transparent,
                    contentColor = AnimeViolet,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFilterIndex]),
                            color = AnimeViolet
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedFilterIndex == index) AnimeViolet else AnimeTextSecondary
                                )
                            }
                        )
                    }
                }
            }

            if (filteredChannels.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "💬", fontSize = 44.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "لا توجد قنوات أو مجموعات في هذا القسم حالياً",
                                color = AnimeTextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ابدأ بإنشاء أول قناة بث أو غرفة أنمي!",
                                color = AnimeViolet,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredChannels, key = { it.id }) { channel ->
                    ChannelCardItem(
                        channel = channel,
                        currentUserId = currentUserId,
                        onClick = { onChannelClick(channel) }
                    )
                }
            }
        }

        // FAB to create a channel or group
        FloatingActionButton(
            onClick = onCreateChannelClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .testTag("create_channel_fab"),
            containerColor = AnimeViolet,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "إنشاء نادٍ / قناة")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "إنشاء نادي/قناة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ChannelCardItem(
    channel: Channel,
    currentUserId: String,
    onClick: () -> Unit
) {
    val isAdmin = channel.admins.contains(currentUserId) || channel.createdBy == currentUserId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("channel_card_${channel.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
        border = BorderStroke(1.dp, if (channel.isBroadcastOnly) AnimeViolet.copy(alpha = 0.5f) else AnimeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (channel.isBroadcastOnly)
                                listOf(AnimeViolet, AnimeCrimson)
                            else
                                listOf(AnimeCyan.copy(alpha = 0.8f), AnimeViolet.copy(alpha = 0.8f))
                        )
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (channel.isBroadcastOnly) Icons.Default.Campaign else Icons.Default.Forum,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = channel.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AnimeTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (channel.isPrivate) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "خاصة",
                                tint = AnimeGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Type Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeCardSurfaceHover)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (channel.isBroadcastOnly) "⚡ بث" else "💬 نقاش",
                                fontSize = 10.sp,
                                color = if (channel.isBroadcastOnly) AnimeViolet else AnimeCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = channel.description,
                    fontSize = 12.sp,
                    color = AnimeTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "💬 ${channel.lastMessageText}",
                        fontSize = 11.sp,
                        color = AnimeCrimson,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isAdmin) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = "مسؤول",
                                    tint = AnimeGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text("مسؤول", fontSize = 9.sp, color = AnimeGold)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = AnimeTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${channel.memberCount}",
                                fontSize = 10.sp,
                                color = AnimeTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = AnimeTextMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
