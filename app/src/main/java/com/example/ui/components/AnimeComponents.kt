package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Comment
import com.example.data.model.Story
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCardSurfaceHover
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeDarkSurface
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeTopAppBar(
    title: String = "بلاك انمي",
    unreadCount: Int = 0,
    onNotificationsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onReelsClick: () -> Unit = {},
    onGamesClick: () -> Unit = {},
    onAiSenseiClick: () -> Unit = {},
    onProfileClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AnimeDarkSurface
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(AnimeCrimson, AnimeViolet))
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.black_anime_app_icon_1791552412791),
                        contentDescription = "شعار بلاك انمي",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = AnimeTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimeCrimson)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "BLACK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = "شبكة الأوتاكو الكبرى",
                        fontSize = 10.sp,
                        color = AnimeViolet
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onAiSenseiClick,
                modifier = Modifier.testTag("app_bar_ai_sensei_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "أوتاكو سينسي AI",
                    tint = AnimeCyan
                )
            }

            IconButton(
                onClick = onReelsClick,
                modifier = Modifier.testTag("app_bar_reels_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = "ريلز الأنمي",
                    tint = AnimeViolet
                )
            }

            IconButton(
                onClick = onGamesClick,
                modifier = Modifier.testTag("app_bar_games_button")
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "ألعاب ومكافآت الأوتاكو",
                    tint = AnimeGold
                )
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("app_bar_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث متقدم",
                    tint = AnimeTextSecondary
                )
            }

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.testTag("app_bar_notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = AnimeCrimson,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "الإشعارات",
                        tint = if (unreadCount > 0) AnimeCrimson else AnimeTextSecondary
                    )
                }
            }

            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.testTag("app_bar_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "الملف الشخصي",
                    tint = AnimeTextSecondary
                )
            }
        }
    )
}

@Composable
fun AnimeAvatar(
    avatarUrl: String?,
    displayName: String,
    size: Int = 44,
    showBorder: Boolean = true
) {
    val borderBrush = Brush.linearGradient(listOf(AnimeCrimson, AnimeViolet))
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .then(
                if (showBorder) Modifier
                    .border(2.dp, borderBrush, CircleShape)
                    .padding(2.dp)
                else Modifier
            )
            .background(AnimeCardSurface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrEmpty()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            // Anime initials or icon
            Text(
                text = displayName.take(1).ifEmpty { "أ" },
                color = AnimeCrimson,
                fontWeight = FontWeight.Bold,
                fontSize = (size / 2.3).sp
            )
        }
    }
}

@Composable
fun AnimeStoryCircle(
    story: Story,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() }
            .testTag("story_item_${story.id}")
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(
                        2.5.dp,
                        Brush.sweepGradient(listOf(AnimeCrimson, AnimeViolet, AnimeGold, AnimeCrimson))
                    ),
                    CircleShape
                )
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            if (story.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = story.imageUrl,
                    contentDescription = story.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.anime_character_art_1791388984389),
                    contentDescription = story.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = story.authorName,
            fontSize = 11.sp,
            color = AnimeTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AddStoryButton(
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() }
            .testTag("add_story_button")
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .border(1.5.dp, AnimeBorder, CircleShape)
                .background(AnimeCardSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AnimeCrimson),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة قصة",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "قصتي",
            fontSize = 11.sp,
            color = AnimeTextPrimary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// Dialog to create a post
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSubmit: (content: String, animeTitle: String, mediaUrl: String, mediaType: String, tags: List<String>) -> Unit,
    onSubmitAdvanced: (content: String, animeTitle: String, mediaUrl: String, mediaType: String, tags: List<String>, isSpoiler: Boolean, pollQ: String, pollOptions: List<String>, rating: Double) -> Unit = { c, t, m, mt, tg, _, _, _, _ -> onSubmit(c, t, m, mt, tg) }
) {
    var content by remember { mutableStateOf("") }
    var animeTitle by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("عام") }
    var selectedMediaType by remember { mutableStateOf("IMAGE") }
    var selectedSampleArt by remember { mutableStateOf("character") }
    var isSpoiler by remember { mutableStateOf(false) }
    var isPollActive by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }
    var pollOption1 by remember { mutableStateOf("") }
    var pollOption2 by remember { mutableStateOf("") }
    var pollOption3 by remember { mutableStateOf("") }
    var ratingScore by remember { mutableStateOf(0.0) }

    val tagsList = listOf("عام", "هجوم العمالقة", "ون بيس", "جوجوتسو كايسن", "سولو ليفلينغ", "قاتل الشياطين", "ديث نوت", "مانجا", "نظريات", "اقتباسات", "حرق_أحداث", "مراجعة_حلقة")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(14.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
            border = BorderStroke(1.dp, AnimeBorder)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "محرر النشر الأسطوري (بلاك انمي)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = AnimeTextPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = AnimeTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = animeTitle,
                        onValueChange = { animeTitle = it },
                        label = { Text("عنوان الأنمي (مثال: ون بيس)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_post_anime_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCrimson,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("اكتب تحليلك، نظريتك، اقتباسك أو مراجعتك...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("create_post_content"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCrimson,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Spoiler & Poll Toggles (Telegram / Reddit / Animesta Features)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSpoiler) AnimeCrimson else AnimeCardSurface)
                                .clickable { isSpoiler = !isSpoiler }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isSpoiler) "⚠️ يحتوي حرق أحداث!" else "تحذير حرق (Spoiler)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpoiler) Color.White else AnimeTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isPollActive) AnimeViolet else AnimeCardSurface)
                                .clickable { isPollActive = !isPollActive }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isPollActive) "📊 استطلاع رأي مفعل" else "إضافة استطلاع رأي",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPollActive) Color.White else AnimeTextSecondary
                            )
                        }
                    }

                    // Poll Fields if active
                    if (isPollActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                            border = BorderStroke(1.dp, AnimeViolet.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("إنشاء استطلاع رأي تفاعلي:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AnimeViolet)
                                OutlinedTextField(
                                    value = pollQuestion,
                                    onValueChange = { pollQuestion = it },
                                    label = { Text("سؤال الاستطلاع") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = pollOption1,
                                    onValueChange = { pollOption1 = it },
                                    label = { Text("الخيار 1 (مثال: نعم)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = pollOption2,
                                    onValueChange = { pollOption2 = it },
                                    label = { Text("الخيار 2 (مثال: لا)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "اختر الوسم الأساسي:",
                        fontSize = 12.sp,
                        color = AnimeTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tagsList.take(8).forEach { tag ->
                            val isSelected = selectedTag == tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AnimeCrimson else AnimeCardSurface)
                                    .clickable { selectedTag = tag }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else AnimeTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "إرفاق صورة الأنمي التعبيرية:",
                        fontSize = 12.sp,
                        color = AnimeTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    if (selectedSampleArt == "character") 2.dp else 1.dp,
                                    if (selectedSampleArt == "character") AnimeCrimson else AnimeBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSampleArt = "character" }
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.anime_character_art_1791388984389),
                                contentDescription = "فن الشخصيات",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    if (selectedSampleArt == "battle") 2.dp else 1.dp,
                                    if (selectedSampleArt == "battle") AnimeCrimson else AnimeBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSampleArt = "battle" }
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.anime_manga_art_1791388998934),
                                contentDescription = "قتالات أسطورية",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    if (selectedSampleArt == "banner") 2.dp else 1.dp,
                                    if (selectedSampleArt == "banner") AnimeCrimson else AnimeBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSampleArt = "banner" }
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.black_anime_banner_1791388840143),
                                contentDescription = "مدينة الأنمي",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                val artIdentifier = selectedSampleArt
                                val pollOpts = if (isPollActive && pollOption1.isNotBlank() && pollOption2.isNotBlank()) {
                                    listOf(pollOption1.trim(), pollOption2.trim())
                                } else emptyList()

                                onSubmitAdvanced(
                                    content.trim(),
                                    animeTitle.ifBlank { "أنمي عام" },
                                    artIdentifier,
                                    selectedMediaType,
                                    listOf(selectedTag),
                                    isSpoiler,
                                    if (isPollActive) pollQuestion.ifBlank { "ما رأيك؟" } else "",
                                    pollOpts,
                                    ratingScore
                                )
                                onDismiss()
                            }
                        },
                        enabled = content.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_create_post_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnimeCrimson,
                            disabledContainerColor = AnimeCardSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("نشر في المجتمع الآن ✨", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// Dialog to create a Story
@Composable
fun CreateStoryDialog(
    onDismiss: () -> Unit,
    onSubmit: (caption: String, animeTag: String, imagePreset: String) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var animeTag by remember { mutableStateOf("هجوم العمالقة") }
    var selectedImage by remember { mutableStateOf("character") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
            border = BorderStroke(1.dp, AnimeBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إضافة قصة أنمي (24 ساعة)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = AnimeTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("تعليق القصة...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_story_caption"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeViolet,
                        unfocusedBorderColor = AnimeBorder,
                        focusedTextColor = AnimeTextPrimary,
                        unfocusedTextColor = AnimeTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = animeTag,
                    onValueChange = { animeTag = it },
                    label = { Text("اسم الأنمي...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeViolet,
                        unfocusedBorderColor = AnimeBorder,
                        focusedTextColor = AnimeTextPrimary,
                        unfocusedTextColor = AnimeTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("اختر مظهر القصة:", fontSize = 12.sp, color = AnimeTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                if (selectedImage == "character") 2.dp else 1.dp,
                                if (selectedImage == "character") AnimeViolet else AnimeBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedImage = "character" }
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.anime_character_art_1791388984389),
                            contentDescription = "شخصية",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                if (selectedImage == "battle") 2.dp else 1.dp,
                                if (selectedImage == "battle") AnimeViolet else AnimeBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedImage = "battle" }
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.anime_manga_art_1791388998934),
                            contentDescription = "قتال",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSubmit(caption.ifBlank { "قصة أنمي مميزة" }, animeTag, selectedImage)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_story_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeViolet),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("نشر القصة الآن", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// Fullscreen Story Viewer
@Composable
fun StoryViewerDialog(
    story: Story,
    onDismiss: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(story) {
        progress = 0f
        val duration = 5000L
        val interval = 50L
        val steps = duration / interval
        val stepIncrement = 1f / steps

        for (i in 0 until steps) {
            delay(interval)
            progress += stepIncrement
        }
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Story Background image
            if (story.imageUrl == "battle") {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.anime_manga_art_1791388998934),
                    contentDescription = story.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (story.imageUrl == "banner") {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.black_anime_banner_1791388840143),
                    contentDescription = story.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.anime_character_art_1791388984389),
                    contentDescription = story.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dark vignette overlay
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

            // Progress bar and header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = AnimeCrimson,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimeAvatar(
                            avatarUrl = story.authorAvatarUrl,
                            displayName = story.authorName,
                            size = 38
                        )
                        Column {
                            Text(
                                text = story.authorName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (story.animeTag.isNotEmpty()) {
                                Text(
                                    text = "#${story.animeTag}",
                                    color = AnimeCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }
            }

            // Bottom Caption & Reaction emojis
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.BottomCenter)
            ) {
                if (story.caption.isNotEmpty()) {
                    Text(
                        text = story.caption,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("🔥", "❤️", "⚔️", "😱", "👏").forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { onDismiss() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

// Comments Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<Comment>,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var newCommentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AnimeDarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "التعليقات (${comments.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = AnimeTextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "كن أول من يعلق على هذا المنشور!",
                        color = AnimeTextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AnimeAvatar(
                                avatarUrl = comment.authorAvatarUrl,
                                displayName = comment.authorName,
                                size = 36
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AnimeCardSurface)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = comment.authorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AnimeCrimson
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = comment.content,
                                    fontSize = 13.sp,
                                    color = AnimeTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Add comment input
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("اكتب تعليقك هنا...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCrimson,
                        unfocusedBorderColor = AnimeBorder,
                        focusedTextColor = AnimeTextPrimary,
                        unfocusedTextColor = AnimeTextPrimary
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            onAddComment(newCommentText.trim())
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AnimeCrimson)
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال التعليق",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// Dialog to create Anime Club / Channel / Group
@Composable
fun CreateChannelDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, category: String, series: String, isBroadcast: Boolean, isPrivate: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("عام") }
    var category by remember { mutableStateOf("مجموعة عامة") }
    var isBroadcast by remember { mutableStateOf(false) }
    var isPrivate by remember { mutableStateOf(false) }

    val categories = listOf("مجموعة نقاش", "قناة بث أخبار", "سلسلة أنمي", "مانجا ونظريات")
    val animeSeriesList = listOf("عام", "ون بيس", "هجوم العمالقة", "جوجوتسو كايسن", "سولو ليفلينغ", "دراغون بول")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
            border = BorderStroke(1.dp, AnimeBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "إنشاء نادي أو قناة أنمي",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = AnimeTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم النادي / القناة (مثال: محبي ون بيس)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_channel_title"),
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف والقواعد...") },
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

                // Type selector: Broadcast vs Discussion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isBroadcast) AnimeCrimson else AnimeCardSurface)
                            .clickable { isBroadcast = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "مجموعة مناقشة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isBroadcast) AnimeViolet else AnimeCardSurface)
                            .clickable { isBroadcast = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "قناة بث (للمسؤولين)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Public vs Private
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isPrivate) AnimeCardSurfaceHover else AnimeCardSurface)
                            .border(1.dp, if (!isPrivate) AnimeGold else AnimeBorder, RoundedCornerShape(10.dp))
                            .clickable { isPrivate = false }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("عامة للجميع", fontSize = 11.sp, color = AnimeTextPrimary)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isPrivate) AnimeCardSurfaceHover else AnimeCardSurface)
                            .border(1.dp, if (isPrivate) AnimeGold else AnimeBorder, RoundedCornerShape(10.dp))
                            .clickable { isPrivate = true }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("خاصة بالدعوة فقط", fontSize = 11.sp, color = AnimeTextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSubmit(title.trim(), description.trim(), if (isBroadcast) "قناة بث" else "مجموعة", series, isBroadcast, isPrivate)
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_channel_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تأسيس النادي الآن", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
