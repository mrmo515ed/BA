package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Channel
import com.example.data.model.Message
import com.example.ui.components.AnimeAvatar
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeDarkSurface
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatRoomScreen(
    channel: Channel,
    messages: List<Message>,
    currentUserId: String,
    onSendMessage: (content: String) -> Unit,
    onSendMessageAdvanced: (content: String, mediaUrl: String, type: String, replyId: String, replySender: String, replyContent: String) -> Unit = { c, _, _, _, _, _ -> onSendMessage(c) },
    onReactToMessage: (messageId: String, emoji: String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Reply state (Telegram & WhatsApp feature)
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var selectedMediaPreset by remember { mutableStateOf("") } // character, battle, banner
    var showStickerPicker by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0B13))
            .testTag("chat_room_screen")
    ) {
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
                        text = "${channel.animeCategory} • ${channel.animeSeries} • ${channel.memberCount} عضو",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id.ifBlank { it.timestamp.toString() } }) { message ->
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
                        modifier = Modifier.fillMaxWidth(0.85f)
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

                        // Message Box
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (isMe) 14.dp else 2.dp,
                                bottomEnd = if (isMe) 2.dp else 14.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMe) AnimeCrimson else AnimeCardSurface
                            ),
                            border = BorderStroke(1.dp, if (isMe) AnimeCrimson else AnimeBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                // Quoted Reply Preview (WhatsApp / Telegram style)
                                if (message.replyToContent.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.25f))
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "رد على ${message.replyToSenderName}:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = AnimeGold
                                            )
                                            Text(
                                                text = message.replyToContent,
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.8f),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // Attached Media or Anime Art (Instagram / Telegram style)
                                if (message.mediaUrl.isNotBlank()) {
                                    val mediaRes = when (message.mediaUrl) {
                                        "character" -> R.drawable.anime_character_art_1791388984389
                                        "battle" -> R.drawable.anime_manga_art_1791388998934
                                        else -> R.drawable.black_anime_banner_1791388840143
                                    }
                                    Image(
                                        painter = painterResource(id = mediaRes),
                                        contentDescription = "وسائط الرسالة",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // Text Content
                                if (message.content.isNotBlank()) {
                                    Text(
                                        text = message.content,
                                        color = if (isMe) Color.White else AnimeTextPrimary,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        // Message footer: Timestamp + Reply Action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = SimpleDateFormat("h:mm a", Locale("ar")).format(Date(message.timestamp)),
                                fontSize = 9.sp,
                                color = AnimeTextMuted
                            )

                            Text(
                                text = "رد",
                                fontSize = 10.sp,
                                color = AnimeCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { replyingToMessage = message }
                                    .padding(horizontal = 4.dp)
                            )
                        }

                        // Reactions display (WhatsApp / Telegram style)
                        if (message.reactions.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                message.reactions.forEach { (emoji, userList) ->
                                    val didIReact = userList.contains(currentUserId)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (didIReact) AnimeCrimson.copy(alpha = 0.3f) else AnimeDarkSurface)
                                            .border(1.dp, if (didIReact) AnimeCrimson else AnimeBorder, RoundedCornerShape(12.dp))
                                            .clickable { onReactToMessage(message.id, emoji) }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$emoji ${userList.size}",
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Reply Box (Telegram / WhatsApp style)
        AnimatedVisibility(visible = replyingToMessage != null) {
            val replyMsg = replyingToMessage
            if (replyMsg != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AnimeDarkSurface)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = "رد",
                            tint = AnimeGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "الرد على ${replyMsg.senderName}:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = AnimeGold
                            )
                            Text(
                                text = replyMsg.content.ifBlank { "وسائط الأنمي" },
                                fontSize = 12.sp,
                                color = AnimeTextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = { replyingToMessage = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء الرد", tint = AnimeTextMuted)
                    }
                }
            }
        }

        // Media attachment preview if chosen
        AnimatedVisibility(visible = selectedMediaPreset.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AnimeDarkSurface)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مرفق فن أنمي تعبيري جاهز للإرسال 🎨",
                    fontSize = 12.sp,
                    color = AnimeCyan,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { selectedMediaPreset = "" }) {
                    Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = AnimeTextSecondary)
                }
            }
        }

        // Quick anime reaction emojis
        val emojis = listOf("🔥", "⚔️", "👑", "💥", "😱", "🍿", "👏", "⚡", "❤️")
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
                        .clickable {
                            val lastMsg = messages.lastOrNull()
                            if (lastMsg != null) {
                                onReactToMessage(lastMsg.id, emoji)
                            } else {
                                onSendMessage(emoji)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(emoji, fontSize = 16.sp)
                }
            }
        }

        // Send input row (Telegram & WhatsApp Style)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(AnimeDarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Media preset selector (Instagram / WhatsApp image attachment)
            IconButton(
                onClick = {
                    selectedMediaPreset = if (selectedMediaPreset.isBlank()) "battle" else ""
                    Toast.makeText(context, if (selectedMediaPreset.isNotBlank()) "تم إرفاق لقطة قتالية أسطورية ⚔️" else "تمت إزالة المرفق", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (selectedMediaPreset.isNotBlank()) AnimeCrimson else AnimeCardSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "إرفاق صورة",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

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

            // Send button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank() || selectedMediaPreset.isNotBlank()) {
                        val reply = replyingToMessage
                        onSendMessageAdvanced(
                            inputText.trim(),
                            selectedMediaPreset,
                            if (selectedMediaPreset.isNotBlank()) "IMAGE" else "TEXT",
                            reply?.id ?: "",
                            reply?.senderName ?: "",
                            reply?.content ?: ""
                        )
                        inputText = ""
                        replyingToMessage = null
                        selectedMediaPreset = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
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
