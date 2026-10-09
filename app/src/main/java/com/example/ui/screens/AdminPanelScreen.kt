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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AdminAuditLog
import com.example.data.model.AnimeItem
import com.example.data.model.ReportItem
import com.example.data.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.components.AnimeAvatar
import com.example.ui.theme.AnimeBlackBg
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCardSurfaceHover
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    currentUserProfile: UserProfile?,
    animes: List<AnimeItem>,
    reports: List<ReportItem>,
    adminLogs: List<AdminAuditLog>,
    allUsers: List<UserProfile>,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    BackHandler { onClose() }

    // Security Gate Verification: Both client profile & current user email verification
    val userEmail = viewModel.currentUser.email ?: ""
    val isAuthorized = userEmail.equals("m774545471@gmail.com", ignoreCase = true) ||
            currentUserProfile?.hasAdminPrivileges(userEmail) == true

    if (!isAuthorized) {
        // Access Denied Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AnimeBlackBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                border = BorderStroke(1.dp, AnimeCrimson),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_access_denied_card")
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AnimeCrimson.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "قفل",
                            tint = AnimeCrimson,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "منطقة إدارية محمية",
                        color = AnimeCrimson,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "لا تملك الصلاحيات الإدارية المطلوبة للوصول إلى لوحة التحكم العليا. هذه الوظيفة محصورة بمسؤول النظام الرئيسي المعتمد (m774545471@gmail.com).",
                        color = AnimeTextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("العودة إلى التطبيق", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    // Authorized Admin View
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("إضافة أنمي جديد", "كتالوج الأنميات", "الإشراف والمستخدمين", "البلاغات والتدقيق")

    // State for Add Anime Form
    var animeIdInput by remember { mutableStateOf("") }
    var titleArabicInput by remember { mutableStateOf("") }
    var titleEnglishInput by remember { mutableStateOf("") }
    var synopsisArabicInput by remember { mutableStateOf("") }
    var synopsisEnglishInput by remember { mutableStateOf("") }
    var episodesCountInput by remember { mutableStateOf("24") }
    var releaseYearInput by remember { mutableStateOf("2026") }
    var seasonInput by remember { mutableStateOf("خريف 2026") }
    var statusInput by remember { mutableStateOf("مستمر") }
    var ratingInput by remember { mutableStateOf("9.0") }
    var selectedCoverPreset by remember { mutableStateOf("battle") }
    var customCoverUrl by remember { mutableStateOf("") }

    val availableGenres = listOf(
        "شونين", "أكشن", "خيال", "غموض", "مغامرات", "كوميديا",
        "رعب", "خوارق", "نفسي", "دراما", "سيوف", "رياضي", "سحر"
    )
    val selectedGenres = remember { mutableStateOf(setOf("شونين", "أكشن")) }
    var isSubmittingAnime by remember { mutableStateOf(false) }
    var showPreviewDialog by remember { mutableStateOf(false) }

    // User moderation dialog state
    var selectedUserForBan by remember { mutableStateOf<UserProfile?>(null) }
    var banReasonInput by remember { mutableStateOf("مخالفة إرشادات المجتمع والنشر") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeBlackBg)
            .testTag("admin_panel_screen")
    ) {
        // Admin Top Header Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AnimeDarkSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AnimeCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "درع الإدارة",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "لوحة الإدارة العليا",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGold)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ROOT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                        Text(
                            text = userEmail,
                            color = AnimeTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Security Verified Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B5E20).copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "آمن",
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "موثق آمن",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = AnimeDarkSurface,
            contentColor = AnimeCrimson,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AnimeCrimson,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (selectedTab == index) AnimeCrimson else AnimeTextSecondary
                        )
                    }
                )
            }
        }

        // Content by Tab
        when (selectedTab) {
            0 -> {
                // Tab 0: Add New Anime Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                        border = BorderStroke(1.dp, AnimeBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "بيانات الأنمي الأساسية",
                                color = AnimeCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            // Anime ID
                            OutlinedTextField(
                                value = animeIdInput,
                                onValueChange = { animeIdInput = it.lowercase().replace(" ", "_") },
                                label = { Text("معرّف الأنمي (Anime ID فريد بالإنجليزية)") },
                                placeholder = { Text("مثال: aot_final أو solo_leveling_s2") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_anime_id_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCrimson,
                                    unfocusedBorderColor = AnimeBorder,
                                    focusedTextColor = AnimeTextPrimary,
                                    unfocusedTextColor = AnimeTextPrimary
                                ),
                                singleLine = true
                            )

                            // Arabic Title
                            OutlinedTextField(
                                value = titleArabicInput,
                                onValueChange = { titleArabicInput = it },
                                label = { Text("عنوان الأنمي بالعربية *") },
                                placeholder = { Text("مثال: هجوم العمالقة: الموسم الأخير") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_anime_title_ar_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCrimson,
                                    unfocusedBorderColor = AnimeBorder,
                                    focusedTextColor = AnimeTextPrimary,
                                    unfocusedTextColor = AnimeTextPrimary
                                ),
                                singleLine = true
                            )

                            // English Title
                            OutlinedTextField(
                                value = titleEnglishInput,
                                onValueChange = { titleEnglishInput = it },
                                label = { Text("عنوان الأنمي بالإنجليزية") },
                                placeholder = { Text("مثال: Attack on Titan: The Final Season") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_anime_title_en_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCrimson,
                                    unfocusedBorderColor = AnimeBorder,
                                    focusedTextColor = AnimeTextPrimary,
                                    unfocusedTextColor = AnimeTextPrimary
                                ),
                                singleLine = true
                            )

                            // Arabic Synopsis
                            OutlinedTextField(
                                value = synopsisArabicInput,
                                onValueChange = { synopsisArabicInput = it },
                                label = { Text("قصة الأنمي باللغة العربية *") },
                                placeholder = { Text("اكتب ملخص القصة والأحداث المشوقة بالعربية...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("admin_anime_synopsis_ar_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCrimson,
                                    unfocusedBorderColor = AnimeBorder,
                                    focusedTextColor = AnimeTextPrimary,
                                    unfocusedTextColor = AnimeTextPrimary
                                ),
                                maxLines = 5
                            )

                            // English Synopsis
                            OutlinedTextField(
                                value = synopsisEnglishInput,
                                onValueChange = { synopsisEnglishInput = it },
                                label = { Text("قصة الأنمي بالإنجليزية (English Synopsis)") },
                                placeholder = { Text("Story synopsis in English...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("admin_anime_synopsis_en_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCrimson,
                                    unfocusedBorderColor = AnimeBorder,
                                    focusedTextColor = AnimeTextPrimary,
                                    unfocusedTextColor = AnimeTextPrimary
                                ),
                                maxLines = 4
                            )
                        }
                    }

                    // Genres & Meta
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                        border = BorderStroke(1.dp, AnimeBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "التصنيفات والأنواع",
                                color = AnimeCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                availableGenres.forEach { genre ->
                                    val isSelected = selectedGenres.value.contains(genre)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedGenres.value = if (isSelected) {
                                                selectedGenres.value - genre
                                            } else {
                                                selectedGenres.value + genre
                                            }
                                        },
                                        label = { Text(genre, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AnimeCrimson,
                                            selectedLabelColor = Color.White,
                                            containerColor = AnimeCardSurface,
                                            labelColor = AnimeTextSecondary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = episodesCountInput,
                                    onValueChange = { episodesCountInput = it },
                                    label = { Text("الحلقات") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AnimeCrimson,
                                        unfocusedBorderColor = AnimeBorder,
                                        focusedTextColor = AnimeTextPrimary,
                                        unfocusedTextColor = AnimeTextPrimary
                                    ),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = releaseYearInput,
                                    onValueChange = { releaseYearInput = it },
                                    label = { Text("السنة") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AnimeCrimson,
                                        unfocusedBorderColor = AnimeBorder,
                                        focusedTextColor = AnimeTextPrimary,
                                        unfocusedTextColor = AnimeTextPrimary
                                    ),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = ratingInput,
                                    onValueChange = { ratingInput = it },
                                    label = { Text("التقييم (1-10)") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AnimeCrimson,
                                        unfocusedBorderColor = AnimeBorder,
                                        focusedTextColor = AnimeTextPrimary,
                                        unfocusedTextColor = AnimeTextPrimary
                                    ),
                                    singleLine = true
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = statusInput,
                                    onValueChange = { statusInput = it },
                                    label = { Text("الحالة (مستمر / مكتمل)") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AnimeCrimson,
                                        unfocusedBorderColor = AnimeBorder,
                                        focusedTextColor = AnimeTextPrimary,
                                        unfocusedTextColor = AnimeTextPrimary
                                    ),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = seasonInput,
                                    onValueChange = { seasonInput = it },
                                    label = { Text("الموسم") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AnimeCrimson,
                                        unfocusedBorderColor = AnimeBorder,
                                        focusedTextColor = AnimeTextPrimary,
                                        unfocusedTextColor = AnimeTextPrimary
                                    ),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Cover Artwork Selector
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                        border = BorderStroke(1.dp, AnimeBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "صورة الغلاف والملصق",
                                color = AnimeCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            Text(
                                text = "اختر قالباً مدمجاً أو أدخل رابط صورة مباشر:",
                                color = AnimeTextSecondary,
                                fontSize = 12.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "battle" to "قتال أسطوري",
                                    "character" to "شخصيات الأنمي",
                                    "banner" to "بانر رئيسي"
                                ).forEach { (preset, label) ->
                                    val isSelected = selectedCoverPreset == preset && customCoverUrl.isBlank()
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) AnimeCrimson.copy(alpha = 0.25f) else AnimeCardSurface
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) AnimeCrimson else AnimeBorder
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedCoverPreset = preset
                                                customCoverUrl = ""
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) AnimeCrimson else AnimeTextPrimary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = customCoverUrl,
                                onValueChange = { customCoverUrl = it },
                                label = { Text("أو رابط صورة مخصص (URL)") },
                                placeholder = { Text("https://example.com/anime.jpg") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCrimson,
                                    unfocusedBorderColor = AnimeBorder,
                                    focusedTextColor = AnimeTextPrimary,
                                    unfocusedTextColor = AnimeTextPrimary
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Action Buttons (Preview & Publish)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showPreviewDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AnimeCyan),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = "معاينة", tint = AnimeCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("معاينة", color = AnimeCyan, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val generatedId = if (animeIdInput.isNotBlank()) {
                                    animeIdInput.trim()
                                } else {
                                    "anime_${System.currentTimeMillis()}"
                                }

                                if (titleArabicInput.isBlank()) {
                                    Toast.makeText(context, "يرجى كتابة عنوان الأنمي بالعربية", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (synopsisArabicInput.isBlank()) {
                                    Toast.makeText(context, "يرجى كتابة قصة الأنمي بالعربية", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                // Check for duplicates in current list
                                val isDuplicate = animes.any { it.id.equals(generatedId, ignoreCase = true) }
                                if (isDuplicate && animeIdInput.isNotBlank()) {
                                    Toast.makeText(context, "معرف الأنمي $generatedId موجود مسبقاً، سيتم تحديثه", Toast.LENGTH_SHORT).show()
                                }

                                isSubmittingAnime = true
                                val finalCover = if (customCoverUrl.isNotBlank()) customCoverUrl else selectedCoverPreset

                                val animeItem = AnimeItem(
                                    id = generatedId,
                                    titleArabic = titleArabicInput.trim(),
                                    titleEnglish = titleEnglishInput.trim().ifBlank { titleArabicInput.trim() },
                                    synopsisArabic = synopsisArabicInput.trim(),
                                    synopsisEnglish = synopsisEnglishInput.trim(),
                                    genres = selectedGenres.value.toList(),
                                    status = statusInput.trim(),
                                    releaseYear = releaseYearInput.toIntOrNull() ?: 2026,
                                    season = seasonInput.trim(),
                                    episodesCount = episodesCountInput.toIntOrNull() ?: 24,
                                    rating = ratingInput.toDoubleOrNull() ?: 9.0,
                                    coverImageUrl = finalCover,
                                    bannerImageUrl = "banner"
                                )

                                viewModel.addOrUpdateAnime(animeItem) { success, message ->
                                    isSubmittingAnime = false
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    if (success) {
                                        // Reset fields
                                        animeIdInput = ""
                                        titleArabicInput = ""
                                        titleEnglishInput = ""
                                        synopsisArabicInput = ""
                                        synopsisEnglishInput = ""
                                        customCoverUrl = ""
                                        selectedTab = 1 // Switch to catalog to see it immediately!
                                    }
                                }
                            },
                            enabled = !isSubmittingAnime,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson),
                            modifier = Modifier
                                .weight(2f)
                                .height(50.dp)
                                .testTag("admin_publish_anime_button")
                        ) {
                            if (isSubmittingAnime) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Add, contentDescription = "نشر")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نشر وتثبيت في التطبيق", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            1 -> {
                // Tab 1: Published Anime Catalog
                if (animes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "أنمي",
                                tint = AnimeTextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = "لا توجد أنميات مضافة في قاعدة البيانات بعد",
                                color = AnimeTextSecondary,
                                fontSize = 15.sp
                            )
                            Button(
                                onClick = { selectedTab = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson)
                            ) {
                                Text("إضافة أول أنمي الآن")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "الأنميات المنشورة في السحابة (${animes.size})",
                                color = AnimeGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        items(animes, key = { it.id }) { anime ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                                border = BorderStroke(1.dp, AnimeBorder),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = anime.titleArabic,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.White
                                            )
                                            if (anime.titleEnglish.isNotBlank() && anime.titleEnglish != anime.titleArabic) {
                                                Text(
                                                    text = anime.titleEnglish,
                                                    fontSize = 12.sp,
                                                    color = AnimeViolet
                                                )
                                            }
                                            Text(
                                                text = "ID: ${anime.id} • ${anime.releaseYear} • ${anime.status}",
                                                fontSize = 11.sp,
                                                color = AnimeTextMuted,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                viewModel.deleteAnime(anime.id, anime.titleArabic) { success, msg ->
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف الأنمي",
                                                tint = AnimeCrimson
                                            )
                                        }
                                    }

                                    if (anime.synopsisArabic.isNotBlank()) {
                                        Text(
                                            text = anime.synopsisArabic,
                                            fontSize = 12.sp,
                                            color = AnimeTextSecondary,
                                            lineHeight = 18.sp,
                                            maxLines = 3,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        anime.genres.forEach { g ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(AnimeCrimson.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(text = g, fontSize = 10.sp, color = AnimeCrimson)
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AnimeGold.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "⭐ ${anime.rating}", fontSize = 10.sp, color = AnimeGold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Users & Moderation
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "المستخدمون المسجلون في بلاك انمي (${allUsers.size})",
                            color = AnimeCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    items(allUsers, key = { it.userId }) { userItem ->
                        val isSelf = userItem.userId == viewModel.currentUserId
                        val isRootAdmin = userItem.email.equals("m774545471@gmail.com", ignoreCase = true)

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (userItem.isBanned) Color(0xFF2A1515) else AnimeDarkSurface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (userItem.isBanned) AnimeCrimson else AnimeBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    AnimeAvatar(
                                        avatarUrl = userItem.avatarUrl,
                                        displayName = userItem.displayName,
                                        size = 42
                                    )

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = userItem.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                            if (userItem.isAdmin || isRootAdmin) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "👑 مدير",
                                                    color = AnimeGold,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Text(
                                            text = "@${userItem.username} • ${userItem.role}",
                                            fontSize = 11.sp,
                                            color = AnimeTextSecondary
                                        )

                                        if (userItem.isBanned) {
                                            Text(
                                                text = "⛔ محظور: ${userItem.banReason}",
                                                color = AnimeCrimson,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (!isSelf && !isRootAdmin) {
                                    if (userItem.isBanned) {
                                        Button(
                                            onClick = {
                                                viewModel.setUserBanStatus(userItem.userId, false, "") { success, msg ->
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("فك الحظر", fontSize = 11.sp)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                selectedUserForBan = userItem
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, AnimeCrimson),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("حظر", color = AnimeCrimson, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Tab 3: Reports & Audit Logs
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "بلاغات المجتمع الحالية (${reports.size})",
                            color = AnimeCrimson,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (reports.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "لا توجد بلاغات معلقة حالياً، المجتمع نظيف ومستقر 🖤",
                                    color = AnimeTextSecondary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(reports, key = { it.id }) { report ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (report.status == "PENDING") Color(0xFF2A1C15) else AnimeDarkSurface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (report.status == "PENDING") AnimeGold else AnimeBorder
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "نوع البلاغ: ${report.targetType}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = AnimeGold
                                        )
                                        Text(
                                            text = report.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (report.status == "PENDING") AnimeGold else Color(0xFF81C784)
                                        )
                                    }

                                    Text(
                                        text = "السبب: ${report.reason}",
                                        color = AnimeTextPrimary,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Text(
                                        text = "المبلغ: ${report.reporterName} • الهدف ID: ${report.targetId}",
                                        color = AnimeTextMuted,
                                        fontSize = 11.sp
                                    )

                                    if (report.status == "PENDING") {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.resolveReport(report.id, "تمت المراجعة واتخاذ الإجراء المناسب") { _, msg ->
                                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("إغلاق البلاغ", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "سجل التدقيق الأمني للعمليات (${adminLogs.size})",
                            color = AnimeCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (adminLogs.isEmpty()) {
                        item {
                            Text(
                                text = "لا توجد عمليات إدارية مسجلة في السجل حتى الآن.",
                                color = AnimeTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        items(adminLogs, key = { it.id }) { log ->
                            val dateStr = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                .format(Date(log.timestamp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                                border = BorderStroke(1.dp, AnimeBorder),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = log.actionType,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = AnimeViolet
                                        )
                                        Text(text = dateStr, fontSize = 10.sp, color = AnimeTextMuted)
                                    }
                                    Text(
                                        text = log.targetTitle,
                                        color = AnimeTextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    Text(
                                        text = "${log.details} (بواسطة: ${log.adminEmail})",
                                        color = AnimeTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Ban User Dialog
    if (selectedUserForBan != null) {
        AlertDialog(
            onDismissRequest = { selectedUserForBan = null },
            title = {
                Text(
                    text = "حظر حساب: ${selectedUserForBan?.displayName}",
                    fontWeight = FontWeight.Bold,
                    color = AnimeCrimson
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "هل أنت متأكد من حظر هذا الحساب؟ لن يتمكن من النشر أو التعليق أو المشاركة في المجتمع.",
                        color = AnimeTextSecondary,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = banReasonInput,
                        onValueChange = { banReasonInput = it },
                        label = { Text("سبب الحظر") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCrimson,
                            unfocusedBorderColor = AnimeBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = selectedUserForBan!!
                        selectedUserForBan = null
                        viewModel.setUserBanStatus(target.userId, true, banReasonInput) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson)
                ) {
                    Text("تأكيد الحظر", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForBan = null }) {
                    Text("إلغاء", color = AnimeTextSecondary)
                }
            },
            containerColor = AnimeDarkSurface
        )
    }

    // Preview Anime Dialog
    if (showPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showPreviewDialog = false },
            title = {
                Text(
                    text = "معاينة بطاقة الأنمي",
                    fontWeight = FontWeight.Bold,
                    color = AnimeGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = titleArabicInput.ifBlank { "عنوان الأنمي بالعربية" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    if (titleEnglishInput.isNotBlank()) {
                        Text(
                            text = titleEnglishInput,
                            fontSize = 13.sp,
                            color = AnimeViolet
                        )
                    }
                    Text(
                        text = "المعرف: ${animeIdInput.ifBlank { "تلقائي" }} • $releaseYearInput • $statusInput • $episodesCountInput حلقة",
                        fontSize = 11.sp,
                        color = AnimeCyan
                    )
                    Text(
                        text = synopsisArabicInput.ifBlank { "قصة الأنمي ستظهر هنا..." },
                        fontSize = 12.sp,
                        color = AnimeTextSecondary,
                        lineHeight = 18.sp
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedGenres.value.forEach { g ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeCrimson.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = g, fontSize = 10.sp, color = AnimeCrimson)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPreviewDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCrimson)
                ) {
                    Text("إغلاق المعاينة")
                }
            },
            containerColor = AnimeDarkSurface
        )
    }
}
