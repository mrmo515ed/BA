package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.LiveComment
import com.example.data.model.LiveStream
import com.example.ui.components.AnimeAvatar
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeDarkSurface
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet

@Composable
fun LiveStreamScreen(
    stream: LiveStream,
    comments: List<LiveComment>,
    currentUserId: String,
    onSendMessage: (String) -> Unit,
    onToggleControls: (isMuted: Boolean, isCameraOff: Boolean) -> Unit,
    onCloseStream: () -> Unit
) {
    BackHandler { onCloseStream() }

    val isHost = stream.hostId == currentUserId
    var commentInput by remember { mutableStateOf("") }
    var isMuted by remember { mutableStateOf(stream.isMuted) }
    var isCameraOff by remember { mutableStateOf(stream.isCameraOff) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_stream_screen")
    ) {
        // Video Stream Background Artwork
        if (isCameraOff) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AnimeDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimeAvatar(
                        avatarUrl = stream.hostAvatarUrl,
                        displayName = stream.hostName,
                        size = 90
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "الكاميرا متوقفة حالياً 📹",
                        color = AnimeTextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            val bgRes = when (stream.previewImage) {
                "character" -> R.drawable.anime_character_art_1791388984389
                "banner" -> R.drawable.black_anime_banner_1791388840143
                else -> R.drawable.anime_manga_art_1791388998934
            }
            Image(
                painter = painterResource(id = bgRes),
                contentDescription = stream.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Dark gradients
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimeAvatar(
                    avatarUrl = stream.hostAvatarUrl,
                    displayName = stream.hostName,
                    size = 40
                )
                Column {
                    Text(
                        text = stream.hostName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stream.title,
                        color = AnimeGold,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // LIVE Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeCrimson)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔴 مباشر",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Viewers count
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "👁️ ${stream.viewersCount}",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }

                // Close / End button
                IconButton(
                    onClick = onCloseStream,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = if (isHost) Icons.Default.CallEnd else Icons.Default.Close,
                        contentDescription = "إنهاء",
                        tint = if (isHost) AnimeCrimson else Color.White
                    )
                }
            }
        }

        // Host controls toolbar (if host)
        if (isHost) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = {
                        isMuted = !isMuted
                        onToggleControls(isMuted, isCameraOff)
                    }
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "كتم الصوت",
                        tint = if (isMuted) AnimeCrimson else Color.White
                    )
                }

                IconButton(
                    onClick = {
                        isCameraOff = !isCameraOff
                        onToggleControls(isMuted, isCameraOff)
                    }
                ) {
                    Icon(
                        imageVector = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "إيقاف الكاميرا",
                        tint = if (isCameraOff) AnimeCrimson else Color.White
                    )
                }
            }
        }

        // Live Comments and Input overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            // Comments stream (last 5 comments)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(comments.takeLast(10), key = { it.id }) { comment ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${comment.senderName}: ",
                                color = AnimeViolet,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = comment.content,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick floating reaction emojis
            val quickReactions = listOf("🔥", "❤️", "⚔️", "👑", "😱", "🍿", "👏")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickReactions) { emoji ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onSendMessage(emoji) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(emoji, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comment text input
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("أرسل تعليقاً في البث...", color = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCrimson,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (commentInput.isNotBlank()) {
                            onSendMessage(commentInput.trim())
                            commentInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AnimeCrimson)
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
}

// Dialog to start live stream
@Composable
fun StartLiveStreamDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, topic: String, preview: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("ون بيس وجوجوتسو كايسن") }
    var preview by remember { mutableStateOf("battle") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AnimeBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "بدء بث مباشر للأنمي 🔴",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = AnimeTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان البث (مثال: نقاش ون بيس المباشر)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCrimson,
                        unfocusedBorderColor = AnimeBorder,
                        focusedTextColor = AnimeTextPrimary,
                        unfocusedTextColor = AnimeTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("موضوع الأنمي الأساسي") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCrimson,
                        unfocusedBorderColor = AnimeBorder,
                        focusedTextColor = AnimeTextPrimary,
                        unfocusedTextColor = AnimeTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSubmit(title.trim(), topic, preview)
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ابدأ البث المباشر الآن 🔴", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
