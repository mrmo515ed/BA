package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.FriendRequest
import com.example.data.model.Post
import com.example.data.model.UserProfile
import com.example.ui.components.AnimeAvatar
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

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    userPosts: List<Post>,
    friendRequests: List<FriendRequest>,
    onAcceptFriendRequest: (FriendRequest) -> Unit,
    onDeclineFriendRequest: (FriendRequest) -> Unit,
    onUpdateProfile: (displayName: String, bio: String, favAnime: String, favChar: String, role: String) -> Unit,
    onOpenEconomyClick: () -> Unit = {},
    onSignOutClick: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showSignOutConfirm by remember { mutableStateOf(false) }
    var showUsersListDialogType by remember { mutableStateOf<String?>(null) } // "FRIENDS", "FOLLOWERS", "FOLLOWING"
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("المنشورات والأنشطة", "الأنمي المفضل", "الأوسمة")

    val user = profile ?: UserProfile()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Banner & Avatar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Cover Image
                Image(
                    painter = painterResource(id = R.drawable.black_anime_banner_1791388840143),
                    contentDescription = "صورة الغلاف",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                // Avatar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp)
                ) {
                    AnimeAvatar(
                        avatarUrl = user.avatarUrl,
                        displayName = user.displayName,
                        size = 80,
                        showBorder = true
                    )
                }

                // Sign Out
                IconButton(
                    onClick = { showSignOutConfirm = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .testTag("sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "تسجيل الخروج",
                        tint = AnimeCrimson
                    )
                }
            }
        }

        // Info & Bio
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.displayName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = AnimeTextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeCrimson.copy(alpha = 0.2f))
                                    .border(1.dp, AnimeCrimson, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = user.role,
                                    fontSize = 10.sp,
                                    color = AnimeCrimson,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "@${user.username}",
                            fontSize = 12.sp,
                            color = AnimeTextMuted
                        )
                    }

                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AnimeViolet),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = AnimeViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تعديل",
                            fontSize = 12.sp,
                            color = AnimeViolet,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = user.bio,
                    fontSize = 13.sp,
                    color = AnimeTextSecondary,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats: Friends, Followers, Following, Posts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AnimeCardSurface)
                        .border(1.dp, AnimeBorder, RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem(title = "المنشورات", count = userPosts.size.toLong(), onClick = {})
                    ProfileStatItem(title = "الأصدقاء 🤝", count = user.friends.size.toLong(), onClick = { showUsersListDialogType = "الأصدقاء" })
                    ProfileStatItem(title = "المتابعون 👥", count = user.followers.size.toLong(), onClick = { showUsersListDialogType = "المتابعون" })
                    ProfileStatItem(title = "يتابع ⚡", count = user.following.size.toLong(), onClick = { showUsersListDialogType = "يتابعهم" })
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Otaku Games & Rewards Entry Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenEconomyClick() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                    border = BorderStroke(1.dp, AnimeGold)
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AnimeGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏆", fontSize = 20.sp)
                            }
                            Column {
                                Text(
                                    text = "نادي ألعاب ومكافآت الأوتاكو",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AnimeTextPrimary
                                )
                                Text(
                                    text = "المكافأة اليومية • كويز الأنمي • بطاقات نادرة",
                                    fontSize = 11.sp,
                                    color = AnimeGold
                                )
                            }
                        }
                        Text(
                            text = "دخول 🎮",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AnimeGold
                        )
                    }
                }
            }
        }

        // Pending Friend Requests Banner
        if (friendRequests.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "طلبات الصداقة المعلقة (${friendRequests.size}) 🤝",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AnimeGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    friendRequests.forEach { req ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                            border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AnimeAvatar(avatarUrl = req.senderAvatarUrl, displayName = req.senderName, size = 34)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = req.senderName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AnimeTextPrimary)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { onAcceptFriendRequest(req) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(AnimeCrimson)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "قبول", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = { onDeclineFriendRequest(req) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(AnimeCardSurfaceHover)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "رفض", tint = AnimeTextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = AnimeCrimson,
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
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) AnimeCrimson else AnimeTextSecondary
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Tab Content
        when (selectedTabIndex) {
            0 -> {
                // Recent Posts & Activity
                if (userPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لم تقم بنشر أي منشور بعد. شارك منشورك الأول!",
                                color = AnimeTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(userPosts, key = { it.id }) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                            border = BorderStroke(1.dp, AnimeBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚔️ ${post.animeTitle}",
                                    fontSize = 11.sp,
                                    color = AnimeGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = post.content,
                                    fontSize = 13.sp,
                                    color = AnimeTextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(text = "❤️ ${post.likesCount}", fontSize = 11.sp, color = AnimeCrimson)
                                    Text(text = "💬 ${post.commentsCount}", fontSize = 11.sp, color = AnimeTextSecondary)
                                    Text(text = "🔁 ${post.sharesCount}", fontSize = 11.sp, color = AnimeViolet)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Favorite Anime Section
                item {
                    val favoriteList = listOf(
                        Triple(user.favoriteAnime, "الأنمي المفضل الأول ⭐ 10/10", AnimeGold),
                        Triple("ون بيس (One Piece)", "مستمر • أسطوري 🔥", AnimeCrimson),
                        Triple("جوجوتسو كايسن (Jujutsu Kaisen)", "مكتمل • رائع ⚡", AnimeCyan),
                        Triple("سولو ليفلينغ (Solo Leveling)", "الموسم الجديد قريباً 🍿", AnimeViolet)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        favoriteList.forEach { (anime, desc, color) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                                border = BorderStroke(1.dp, AnimeBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Movie,
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = anime,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AnimeTextPrimary
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 11.sp,
                                                color = color
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AnimeGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Badges
                item {
                    val badges = listOf(
                        Pair("عضو مؤسس 🖤", "من أوائل المنضمين لشبكة بلاك انمي"),
                        Pair("مذيع مباشر 🔴", "بدأ بثوثاً مباشرة لمناقشة الأنمي"),
                        Pair("ناقد شونين ⚔️", "تفاعل وشارك أكثر من 10 مراجعات ونظريات"),
                        Pair("صانع قصص ⚡", "نشر قصص وحالات أنمي مميزة")
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        badges.forEach { (badge, desc) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                                border = BorderStroke(1.dp, AnimeBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MilitaryTech,
                                        contentDescription = null,
                                        tint = AnimeGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = badge,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AnimeTextPrimary
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 11.sp,
                                            color = AnimeTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Users List Dialog (Friends / Followers / Following)
    if (showUsersListDialogType != null) {
        Dialog(onDismissRequest = { showUsersListDialogType = null }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
                border = BorderStroke(1.dp, AnimeBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "قائمة ${showUsersListDialogType!!}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AnimeTextPrimary
                        )
                        IconButton(onClick = { showUsersListDialogType = null }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = AnimeTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "يتم تحديث قائمة ${showUsersListDialogType!!} ومزامنتها مباشرة عبر قاعدة البيانات السحابية.",
                        fontSize = 12.sp,
                        color = AnimeTextSecondary
                    )
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        var editName by remember { mutableStateOf(user.displayName) }
        var editBio by remember { mutableStateOf(user.bio) }
        var editFavAnime by remember { mutableStateOf(user.favoriteAnime) }
        var editFavChar by remember { mutableStateOf(user.favoriteCharacter) }
        var editRole by remember { mutableStateOf(user.role) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                border = BorderStroke(1.dp, AnimeBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "تعديل الملف الشخصي ✏️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AnimeTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("الاسم الظاهر") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeViolet,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("النبذة التعريفية") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeViolet,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editFavAnime,
                        onValueChange = { editFavAnime = it },
                        label = { Text("الأنمي المفضل") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeViolet,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editFavChar,
                        onValueChange = { editFavChar = it },
                        label = { Text("الشخصية المفضلة") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeViolet,
                            unfocusedBorderColor = AnimeBorder,
                            focusedTextColor = AnimeTextPrimary,
                            unfocusedTextColor = AnimeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onUpdateProfile(editName, editBio, editFavAnime, editFavChar, editRole)
                            showEditDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_profile_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeViolet)
                    ) {
                        Text("حفظ التعديلات", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // Sign Out Dialog
    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            title = { Text("تسجيل الخروج", color = AnimeTextPrimary) },
            text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج من حسابك في بلاك انمي؟", color = AnimeTextSecondary) },
            containerColor = AnimeCardSurface,
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutConfirm = false
                        onSignOutClick()
                    }
                ) {
                    Text("نعم، خروج", color = AnimeCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) {
                    Text("إلغاء", color = AnimeTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProfileStatItem(title: String, count: Long, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = count.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = AnimeTextPrimary
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = AnimeTextSecondary
        )
    }
}
