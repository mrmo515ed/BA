package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Story
import com.example.ui.components.AnimeTopAppBar
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreateChannelDialog
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.CreateStoryDialog
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.ChannelsScreen
import com.example.ui.screens.ChatRoomScreen
import com.example.ui.screens.EconomyGameScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.LiveStreamScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.StartLiveStreamDialog
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeDarkSurface
import com.example.ui.theme.AnimeTextSecondary

enum class AnimeTab(val label: String) {
    FEED("الرئيسية"),
    EXPLORE("استكشاف"),
    CHANNELS("القنوات والنوادي"),
    NOTIFICATIONS("الإشعارات"),
    PROFILE("حسابي")
}

@Composable
fun AnimeMainApp(
    viewModel: MainViewModel,
    onSignOutClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AnimeTab.FEED) }

    // Dialog states
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var showCreateStoryDialog by remember { mutableStateOf(false) }
    var showCreateChannelDialog by remember { mutableStateOf(false) }
    var showStartLiveDialog by remember { mutableStateOf(false) }
    var activeStoryToView by remember { mutableStateOf<Story?>(null) }
    var showReelsScreen by remember { mutableStateOf(false) }
    var showEconomyScreen by remember { mutableStateOf(false) }

    // State flows
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val liveStreams by viewModel.liveStreams.collectAsStateWithLifecycle()
    val activeLiveStream by viewModel.activeLiveStream.collectAsStateWithLifecycle()
    val liveComments by viewModel.liveComments.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val friendRequests by viewModel.friendRequests.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeChannel by viewModel.activeChannel.collectAsStateWithLifecycle()
    val channelMessages by viewModel.channelMessages.collectAsStateWithLifecycle()
    val activePostForComments by viewModel.activePost.collectAsStateWithLifecycle()
    val postComments by viewModel.postComments.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val reels by viewModel.reels.collectAsStateWithLifecycle()
    val economyProfile by viewModel.economyProfile.collectAsStateWithLifecycle()

    // BackHandler for tab navigation
    if (selectedTab != AnimeTab.FEED && activeChannel == null && activeLiveStream == null && !showReelsScreen && !showEconomyScreen) {
        BackHandler { selectedTab = AnimeTab.FEED }
    }

    // Active Live Stream Screen (Full Screen)
    if (activeLiveStream != null) {
        LiveStreamScreen(
            stream = activeLiveStream!!,
            comments = liveComments,
            currentUserId = viewModel.currentUserId,
            onSendMessage = { content -> viewModel.sendLiveComment(content) },
            onToggleControls = { isMuted, isCameraOff -> viewModel.toggleLiveControls(isMuted, isCameraOff) },
            onCloseStream = { viewModel.closeLiveStream() }
        )
        return
    }

    // Active Channel Chat Room (Full Screen)
    if (activeChannel != null) {
        ChatRoomScreen(
            channel = activeChannel!!,
            messages = channelMessages,
            currentUserId = viewModel.currentUserId,
            onSendMessage = { content ->
                viewModel.sendMessage(activeChannel!!.id, content)
            },
            onBackClick = { viewModel.closeChannel() }
        )
        return
    }

    // Active Reels Screen (Full Screen)
    if (showReelsScreen) {
        ReelsScreen(
            reels = reels,
            currentUserId = viewModel.currentUserId,
            onLikeClick = { reelId -> viewModel.toggleLikeReel(reelId) },
            onCreateReelClick = { caption, animeTitle, previewRes ->
                viewModel.createReel(caption, animeTitle, previewRes)
            },
            onClose = { showReelsScreen = false }
        )
        return
    }

    // Active Economy & Games Screen (Full Screen)
    if (showEconomyScreen) {
        EconomyGameScreen(
            economy = economyProfile,
            onClaimDailyReward = { viewModel.claimDailyReward() },
            onClose = { showEconomyScreen = false }
        )
        return
    }

    Scaffold(
        topBar = {
            AnimeTopAppBar(
                title = "بلاك انمي",
                unreadCount = unreadNotificationsCount,
                onNotificationsClick = { selectedTab = AnimeTab.NOTIFICATIONS },
                onSearchClick = { selectedTab = AnimeTab.EXPLORE },
                onReelsClick = { showReelsScreen = true },
                onGamesClick = { showEconomyScreen = true },
                onProfileClick = { selectedTab = AnimeTab.PROFILE }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AnimeDarkSurface,
                modifier = Modifier.testTag("main_bottom_nav_bar")
            ) {
                // Feed
                NavigationBarItem(
                    selected = selectedTab == AnimeTab.FEED,
                    onClick = { selectedTab = AnimeTab.FEED },
                    icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                    label = { Text(AnimeTab.FEED.label, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeCrimson,
                        selectedTextColor = AnimeCrimson,
                        unselectedIconColor = AnimeTextSecondary,
                        unselectedTextColor = AnimeTextSecondary,
                        indicatorColor = AnimeCrimson.copy(alpha = 0.15f)
                    )
                )

                // Explore
                NavigationBarItem(
                    selected = selectedTab == AnimeTab.EXPLORE,
                    onClick = { selectedTab = AnimeTab.EXPLORE },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "استكشاف") },
                    label = { Text(AnimeTab.EXPLORE.label, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeCrimson,
                        selectedTextColor = AnimeCrimson,
                        unselectedIconColor = AnimeTextSecondary,
                        unselectedTextColor = AnimeTextSecondary,
                        indicatorColor = AnimeCrimson.copy(alpha = 0.15f)
                    )
                )

                // Channels
                NavigationBarItem(
                    selected = selectedTab == AnimeTab.CHANNELS,
                    onClick = { selectedTab = AnimeTab.CHANNELS },
                    icon = { Icon(Icons.Default.Forum, contentDescription = "القنوات") },
                    label = { Text(AnimeTab.CHANNELS.label, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeCrimson,
                        selectedTextColor = AnimeCrimson,
                        unselectedIconColor = AnimeTextSecondary,
                        unselectedTextColor = AnimeTextSecondary,
                        indicatorColor = AnimeCrimson.copy(alpha = 0.15f)
                    )
                )

                // Notifications
                NavigationBarItem(
                    selected = selectedTab == AnimeTab.NOTIFICATIONS,
                    onClick = { selectedTab = AnimeTab.NOTIFICATIONS },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(containerColor = AnimeCrimson) {
                                        Text(
                                            text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                                            fontSize = 9.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "الإشعارات")
                        }
                    },
                    label = { Text(AnimeTab.NOTIFICATIONS.label, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeCrimson,
                        selectedTextColor = AnimeCrimson,
                        unselectedIconColor = AnimeTextSecondary,
                        unselectedTextColor = AnimeTextSecondary,
                        indicatorColor = AnimeCrimson.copy(alpha = 0.15f)
                    )
                )

                // Profile
                NavigationBarItem(
                    selected = selectedTab == AnimeTab.PROFILE,
                    onClick = { selectedTab = AnimeTab.PROFILE },
                    icon = { Icon(Icons.Default.Person, contentDescription = "حسابي") },
                    label = { Text(AnimeTab.PROFILE.label, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeCrimson,
                        selectedTextColor = AnimeCrimson,
                        unselectedIconColor = AnimeTextSecondary,
                        unselectedTextColor = AnimeTextSecondary,
                        indicatorColor = AnimeCrimson.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AnimeTab.FEED -> {
                    FeedScreen(
                        posts = posts,
                        stories = stories,
                        liveStreams = liveStreams,
                        currentUserProfile = userProfile,
                        currentUserId = viewModel.currentUserId,
                        onLikeClick = { postId -> viewModel.toggleLike(postId) },
                        onCommentClick = { post -> viewModel.openComments(post) },
                        onReShareClick = { postId -> viewModel.reSharePost(postId) },
                        onStoryClick = { story -> activeStoryToView = story },
                        onLiveStreamClick = { stream -> viewModel.openLiveStream(stream) },
                        onStartLiveClick = { showStartLiveDialog = true },
                        onAddStoryClick = { showCreateStoryDialog = true },
                        onCreatePostClick = { showCreatePostDialog = true }
                    )
                }

                AnimeTab.EXPLORE -> {
                    ExploreScreen(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        posts = posts,
                        channels = channels,
                        allUsers = allUsers,
                        currentUserProfile = userProfile,
                        currentUserId = viewModel.currentUserId,
                        onFollowToggle = { targetId -> viewModel.toggleFollowUser(targetId) },
                        onSendFriendRequest = { targetId -> viewModel.sendFriendRequest(targetId) },
                        onPostClick = { post -> viewModel.openComments(post) },
                        onChannelClick = { channel -> viewModel.openChannel(channel) }
                    )
                }

                AnimeTab.CHANNELS -> {
                    ChannelsScreen(
                        channels = channels,
                        currentUserId = viewModel.currentUserId,
                        onChannelClick = { channel -> viewModel.openChannel(channel) },
                        onCreateChannelClick = { showCreateChannelDialog = true }
                    )
                }

                AnimeTab.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onNotificationClick = { item -> viewModel.markNotificationRead(item.id) },
                        onMarkAllReadClick = { viewModel.markAllNotificationsRead() }
                    )
                }

                AnimeTab.PROFILE -> {
                    val myPosts = posts.filter { it.authorId == viewModel.currentUserId }
                    ProfileScreen(
                        profile = userProfile,
                        userPosts = myPosts,
                        friendRequests = friendRequests,
                        onAcceptFriendRequest = { req -> viewModel.respondToFriendRequest(req.id, true, req.senderId) },
                        onDeclineFriendRequest = { req -> viewModel.respondToFriendRequest(req.id, false, req.senderId) },
                        onUpdateProfile = { name, bio, favAnime, favChar, role ->
                            viewModel.updateProfile(name, bio, favAnime, favChar, role)
                        },
                        onOpenEconomyClick = { showEconomyScreen = true },
                        onSignOutClick = onSignOutClick
                    )
                }
            }
        }
    }

    // Story Viewer Dialog
    if (activeStoryToView != null) {
        StoryViewerDialog(
            story = activeStoryToView!!,
            onDismiss = { activeStoryToView = null }
        )
    }

    // Create Post Dialog
    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onSubmit = { content, animeTitle, mediaUrl, mediaType, tags ->
                viewModel.createPost(content, animeTitle, mediaUrl, mediaType, tags)
            }
        )
    }

    // Create Story Dialog
    if (showCreateStoryDialog) {
        CreateStoryDialog(
            onDismiss = { showCreateStoryDialog = false },
            onSubmit = { caption, animeTag, imagePreset ->
                viewModel.createStory(imagePreset, caption, animeTag)
            }
        )
    }

    // Create Channel / Group Dialog
    if (showCreateChannelDialog) {
        CreateChannelDialog(
            onDismiss = { showCreateChannelDialog = false },
            onSubmit = { title, description, category, series, isBroadcast, isPrivate ->
                viewModel.createBroadcastOrGroup(title, description, category, series, isBroadcast, isPrivate)
            }
        )
    }

    // Start Live Stream Dialog
    if (showStartLiveDialog) {
        StartLiveStreamDialog(
            onDismiss = { showStartLiveDialog = false },
            onSubmit = { title, topic, preview ->
                viewModel.startLiveStream(title, topic, preview)
            }
        )
    }

    // Comments Bottom Sheet
    if (activePostForComments != null) {
        CommentsBottomSheet(
            comments = postComments,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { content ->
                viewModel.addComment(activePostForComments!!.id, content)
            }
        )
    }
}
