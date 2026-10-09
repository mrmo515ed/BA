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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AnimeItem
import com.example.data.model.Post
import com.example.data.model.UserAnimeTracking
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCardSurfaceHover
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeDarkSurface
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeNeonPurple
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary
import com.example.ui.theme.AnimeViolet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeDetailScreen(
    anime: AnimeItem,
    tracking: UserAnimeTracking?,
    relatedPosts: List<Post>,
    onUpdateTracking: (status: String, currentEp: Int, totalEp: Int, score: Double, note: String) -> Unit,
    onCreateDiscussionPost: (animeTitle: String) -> Unit,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("نظرة عامة والقصة", "تتبع الحلقات والتقييم", "مناقشات ونظريات المجتمع")

    var showTrackingDialog by remember { mutableStateOf(false) }

    // Cover image resource resolution
    val imageRes = when (anime.coverImageUrl) {
        "character" -> R.drawable.anime_character_art_1791388984389
        "banner" -> R.drawable.black_anime_banner_1791388840143
        else -> R.drawable.anime_manga_art_1791388998934
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0B13))
            .testTag("anime_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Hero Banner & Poster
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    if (anime.coverImageUrl.startsWith("http")) {
                        AsyncImage(
                            model = anime.coverImageUrl,
                            contentDescription = anime.titleArabic,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = anime.titleArabic,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Dark gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color(0xFF0F0B13).copy(alpha = 0.85f),
                                        Color(0xFF0F0B13)
                                    )
                                )
                            )
                    )

                    // Top Bar Back & Share Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 28.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "تم نسخ رابط الأنمي للمشاركة 🔗", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "مشاركة",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Title & Meta inside Hero Bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeCrimson)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = anime.status,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeGold.copy(alpha = 0.2f))
                                    .border(1.dp, AnimeGold, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AnimeGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${anime.rating} / 10",
                                        color = AnimeGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "${anime.releaseYear} • ${anime.episodesCount} حلقة",
                                color = AnimeTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = anime.titleArabic,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )

                        if (anime.titleEnglish.isNotBlank()) {
                            Text(
                                text = anime.titleEnglish,
                                fontSize = 13.sp,
                                color = AnimeTextSecondary
                            )
                        }
                    }
                }
            }

            // Quick Tracking Action Bar (Kunaiu & Animesta Style)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                    border = BorderStroke(1.dp, AnimeBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val currentStatusAr = when (tracking?.status) {
                                "WATCHING" -> "أشاهده حالياً 🍿"
                                "COMPLETED" -> "تم إنهاؤه بالكامل 🏆"
                                "PLAN_TO_WATCH" -> "مخطط لمشاهدته 📝"
                                "DROPPED" -> "متوقف ⏸️"
                                else -> "لم تتم الإضافة لقائمتك بعد"
                            }
                            Text(
                                text = "حالة متابعتك في كونايو وبلاك انمي:",
                                fontSize = 11.sp,
                                color = AnimeTextMuted
                            )
                            Text(
                                text = currentStatusAr,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tracking != null) AnimeCyan else AnimeTextSecondary
                            )
                            if (tracking != null) {
                                Text(
                                    text = "الحلقة: ${tracking.currentEpisode} من ${anime.episodesCount} • تقييمك: ${if (tracking.userScore > 0) "${tracking.userScore}⭐" else "لم يحدد"}",
                                    fontSize = 11.sp,
                                    color = AnimeGold
                                )
                            }
                        }

                        Button(
                            onClick = { showTrackingDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (tracking != null) Icons.Default.Edit else Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (tracking != null) "تعديل التقدم" else "إضافة لقائمتي",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tabs Header
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = AnimeCrimson,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AnimeCrimson
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) AnimeCrimson else AnimeTextSecondary
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Tab 0: Overview & Synopsis (Arabic and English)
            if (selectedTab == 0) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Genres FlowRow
                        if (anime.genres.isNotEmpty()) {
                            Text(
                                text = "التصنيفات والأنواع:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnimeTextPrimary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                anime.genres.forEach { genre ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AnimeCardSurface)
                                            .border(1.dp, AnimeBorder, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "#$genre",
                                            fontSize = 11.sp,
                                            color = AnimeViolet,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Arabic Synopsis Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                            border = BorderStroke(1.dp, AnimeBorder),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = AnimeCrimson,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "القصة الرسمية باللغة العربية",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AnimeTextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = anime.synopsisArabic.ifBlank { "قصة الأنمي لم تتوفر بعد بالعربية." },
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = AnimeTextSecondary
                                )
                            }
                        }

                        // English Synopsis Card
                        if (anime.synopsisEnglish.isNotBlank()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                                border = BorderStroke(1.dp, AnimeBorder),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Synopsis in English",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AnimeCyan
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = anime.synopsisEnglish,
                                        fontSize = 12.sp,
                                        lineHeight = 20.sp,
                                        color = AnimeTextSecondary
                                    )
                                }
                            }
                        }

                        // Anime Info Metadata Grid
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "تفاصيل العمل والإنتاج:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AnimeTextPrimary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("الموسم والسنة:", fontSize = 12.sp, color = AnimeTextMuted)
                                    Text("${anime.season} (${anime.releaseYear})", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("عدد الحلقات الإجمالي:", fontSize = 12.sp, color = AnimeTextMuted)
                                    Text("${anime.episodesCount} حلقة", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("الحالة الراهنة:", fontSize = 12.sp, color = AnimeTextMuted)
                                    Text(anime.status, fontSize = 12.sp, color = AnimeCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 1: Episodes and Tracking Manager (Kunaiu episode-by-episode progress)
            if (selectedTab == 1) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Quick Progress Bar
                        val currentEp = tracking?.currentEpisode ?: 0
                        val progressFraction = if (anime.episodesCount > 0) (currentEp.toFloat() / anime.episodesCount).coerceIn(0f, 1f) else 0f

                        Card(
                            colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                            border = BorderStroke(1.dp, AnimeBorder),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "مستوى إنجازك:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AnimeTextPrimary
                                    )
                                    Text(
                                        text = "$currentEp / ${anime.episodesCount} حلقة (${(progressFraction * 100).toInt()}%)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AnimeGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = AnimeCrimson,
                                    trackColor = AnimeDarkSurface
                                )
                            }
                        }

                        Text(
                            text = "قائمة الحلقات وتحديد ما شاهدته (كونايو):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AnimeTextPrimary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }

                // Episodes List with instant checkmark
                val totalEp = anime.episodesCount.coerceAtLeast(1)
                items(totalEp) { index ->
                    val epNumber = index + 1
                    val isWatched = (tracking?.currentEpisode ?: 0) >= epNumber

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isWatched) AnimeCardSurface.copy(alpha = 0.6f) else AnimeCardSurface
                        ),
                        border = BorderStroke(1.dp, if (isWatched) AnimeCyan.copy(alpha = 0.5f) else AnimeBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isWatched) AnimeCyan else AnimeDarkSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = epNumber.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isWatched) Color.Black else Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "الحلقة رقم $epNumber",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isWatched) AnimeTextSecondary else AnimeTextPrimary
                                    )
                                    Text(
                                        text = if (isWatched) "تمت المشاهدة ✓" else "لم تشاهد بعد",
                                        fontSize = 11.sp,
                                        color = if (isWatched) AnimeCyan else AnimeTextMuted
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    val newWatched = if (isWatched) epNumber - 1 else epNumber
                                    val newStatus = if (newWatched >= anime.episodesCount) "COMPLETED" else "WATCHING"
                                    onUpdateTracking(
                                        newStatus,
                                        newWatched,
                                        anime.episodesCount,
                                        tracking?.userScore ?: 9.0,
                                        tracking?.note ?: ""
                                    )
                                    Toast.makeText(context, "تم تحديث التقدم إلى الحلقة $newWatched 🍿", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isWatched) AnimeCyan.copy(alpha = 0.2f) else AnimeDarkSurface)
                            ) {
                                Icon(
                                    imageVector = if (isWatched) Icons.Default.Check else Icons.Default.PlayArrow,
                                    contentDescription = "تحديد",
                                    tint = if (isWatched) AnimeCyan else AnimeTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tab 2: Community Discussions and Theories (Animesta / Anime Mirai)
            if (selectedTab == 2) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onCreateDiscussionPost(anime.titleArabic) },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeViolet),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("فتح مناقشة أو كتابة نظرية حول ${anime.titleArabic}", fontWeight = FontWeight.Bold)
                        }

                        if (relatedPosts.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "كن أول من يكتب نظرية أو مراجعة حول هذا العمل الأسطوري! 🖤✨",
                                        color = AnimeTextSecondary,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                items(relatedPosts, key = { it.id }) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                        border = BorderStroke(1.dp, AnimeBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = post.authorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AnimeTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${post.authorRole}",
                                    fontSize = 11.sp,
                                    color = AnimeGold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = post.content,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = AnimeTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    // Tracking Setup Dialog
    if (showTrackingDialog) {
        var selectedStatus by remember { mutableStateOf(tracking?.status ?: "WATCHING") }
        var episodeInput by remember { mutableStateOf((tracking?.currentEpisode ?: 1).toString()) }
        var scoreInput by remember { mutableStateOf((tracking?.userScore ?: 9.0).toString()) }
        var noteInput by remember { mutableStateOf(tracking?.note ?: "") }

        AlertDialog(
            onDismissRequest = { showTrackingDialog = false },
            title = {
                Text(
                    text = "تتبع أنمي ${anime.titleArabic}",
                    fontWeight = FontWeight.Bold,
                    color = AnimeTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("اختر الحالة:", fontSize = 12.sp, color = AnimeTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "WATCHING" to "أشاهده",
                            "COMPLETED" to "أنهيته",
                            "PLAN_TO_WATCH" to "مخطط"
                        ).forEach { (st, label) ->
                            val isSel = selectedStatus == st
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) AnimeCrimson else AnimeDarkSurface)
                                    .clickable { selectedStatus = st }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.White else AnimeTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = episodeInput,
                        onValueChange = { episodeInput = it },
                        label = { Text("وصلت إلى الحلقة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCrimson,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = scoreInput,
                        onValueChange = { scoreInput = it },
                        label = { Text("تقييمك الشخصي (من 10)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCrimson,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("ملاحظتك الخاصة...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCrimson,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ep = episodeInput.toIntOrNull() ?: 1
                        val sc = scoreInput.toDoubleOrNull() ?: 0.0
                        onUpdateTracking(selectedStatus, ep, anime.episodesCount, sc, noteInput)
                        showTrackingDialog = false
                        Toast.makeText(context, "تم حفظ بيانات التتبع في حسابك بنجاح ✨", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson)
                ) {
                    Text("حفظ التتبع", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTrackingDialog = false }) {
                    Text("إلغاء", color = AnimeTextSecondary)
                }
            },
            containerColor = AnimeDarkSurface
        )
    }
}
