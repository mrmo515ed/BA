package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.Channel
import com.example.data.model.Message
import com.example.ui.components.AnimeAvatar
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeDarkSurface
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    channel: Channel,
    messages: List<Message>,
    currentUserId: String,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit
) {
    // Mandatory BackHandler for secondary screens
    BackHandler { onBackClick() }

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header TopAppBar
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AnimeDarkSurface),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = AnimeTextPrimary
                    )
                }
            },
            title = {
                Column {
                    Text(
                        text = channel.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AnimeTextPrimary
                    )
                    Text(
                        text = "${channel.animeCategory} • ${channel.memberCount} عضو نشط",
                        fontSize = 11.sp,
                        color = AnimeGold
                    )
                }
            }
        )

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                val isMe = message.senderId == currentUserId

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    if (!isMe) {
                        AnimeAvatar(
                            avatarUrl = message.senderAvatarUrl,
                            displayName = message.senderName,
                            size = 32
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        if (!isMe) {
                            Text(
                                text = message.senderName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnimeViolet,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isMe) 14.dp else 2.dp,
                                        bottomEnd = if (isMe) 2.dp else 14.dp
                                    )
                                )
                                .background(if (isMe) AnimeCrimson else AnimeCardSurface)
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = message.content,
                                color = if (isMe) Color.White else AnimeTextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 19.sp
                            )
                        }

                        Text(
                            text = SimpleDateFormat("h:mm a", Locale("ar")).format(Date(message.timestamp)),
                            fontSize = 9.sp,
                            color = AnimeTextMuted,
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp)
                        )
                    }
                }
            }
        }

        // Quick anime reaction emojis
        val emojis = listOf("🔥", "⚔️", "👑", "💥", "😱", "🍿", "👏", "⚡")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(AnimeDarkSurface)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AnimeCardSurface)
                        .clickable { onSendMessage(emoji) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(emoji, fontSize = 16.sp)
                }
            }
        }

        // Send input row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(AnimeDarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("اكتب رسالتك في النادي...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
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

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText.trim())
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AnimeCrimson)
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "إرسال",
                    tint = Color.White
                )
            }
        }
    }
}
