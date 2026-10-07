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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.ui.components.AnimeAvatar
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onNotificationClick: (NotificationItem) -> Unit,
    onMarkAllReadClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("الكل") }
    val filters = listOf("الكل", "التفاعلات", "التعليقات", "النظام")

    val filteredList = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            "التفاعلات" -> notifications.filter { it.type == "LIKE" }
            "التعليقات" -> notifications.filter { it.type == "COMMENT" }
            "النظام" -> notifications.filter { it.type == "ANNOUNCEMENT" }
            else -> notifications
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notifications_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مركز الإشعارات الفورية ⚡",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = AnimeTextPrimary
                    )
                    Text(
                        text = "تحديثات التفاعل والمحادثات وإعلانات الأنمي الحصرية",
                        fontSize = 11.sp,
                        color = AnimeTextSecondary
                    )
                }

                IconButton(
                    onClick = onMarkAllReadClick,
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "تحديد الكل كمقروء",
                        tint = AnimeCrimson
                    )
                }
            }
        }

        // Filters row
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AnimeCrimson else AnimeCardSurface)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else AnimeTextSecondary
                        )
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔔", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "لا توجد إشعارات جديدة حالياً",
                            color = AnimeTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { item ->
                NotificationRowItem(
                    item = item,
                    onClick = { onNotificationClick(item) }
                )
            }
        }
    }
}

@Composable
fun NotificationRowItem(
    item: NotificationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!item.isRead) AnimeCardSurfaceHover else AnimeCardSurface
        ),
        border = BorderStroke(1.dp, if (!item.isRead) AnimeCrimson.copy(alpha = 0.5f) else AnimeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type icon badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.type) {
                            "LIKE" -> AnimeCrimson.copy(alpha = 0.2f)
                            "COMMENT" -> AnimeViolet.copy(alpha = 0.2f)
                            "FOLLOW" -> AnimeCyan.copy(alpha = 0.2f)
                            else -> AnimeGold.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.type) {
                        "LIKE" -> Icons.Default.Favorite
                        "COMMENT" -> Icons.Default.ChatBubble
                        "FOLLOW" -> Icons.Default.PersonAdd
                        else -> Icons.Default.Campaign
                    },
                    contentDescription = null,
                    tint = when (item.type) {
                        "LIKE" -> AnimeCrimson
                        "COMMENT" -> AnimeViolet
                        "FOLLOW" -> AnimeCyan
                        else -> AnimeGold
                    },
                    modifier = Modifier.size(20.dp)
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
                        text = item.title,
                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = AnimeTextPrimary
                    )

                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AnimeCrimson)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.message,
                    fontSize = 12.sp,
                    color = AnimeTextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatNotificationTime(item.createdAt),
                    fontSize = 10.sp,
                    color = AnimeTextMuted
                )
            }
        }
    }
}

private fun formatNotificationTime(timeMillis: Long): String {
    val diff = System.currentTimeMillis() - timeMillis
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "الآن"
        minutes < 60 -> "قبل $minutes دقيقة"
        hours < 24 -> "قبل $hours ساعة"
        days < 7 -> "قبل $days يوم"
        else -> SimpleDateFormat("d MMM", Locale("ar")).format(Date(timeMillis))
    }
}
