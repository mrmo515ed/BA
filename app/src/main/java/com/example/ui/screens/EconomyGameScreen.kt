package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EconomyProfile
import com.example.ui.theme.AnimeBorder
import com.example.ui.theme.AnimeCardSurface
import com.example.ui.theme.AnimeCardSurfaceHover
import com.example.ui.theme.AnimeCrimson
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeNeonPurple
import com.example.ui.theme.AnimeSuccessGreen
import com.example.ui.theme.AnimeTextMuted
import com.example.ui.theme.AnimeTextPrimary
import com.example.ui.theme.AnimeTextSecondary

@Composable
fun EconomyGameScreen(
    economy: EconomyProfile,
    onClaimDailyReward: () -> Unit,
    onClose: (() -> Unit)? = null
) {
    if (onClose != null) {
        BackHandler { onClose() }
    }

    val context = LocalContext.current

    // Quiz mini-game state
    var quizQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableIntStateOf(-1) }
    var quizScore by remember { mutableIntStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }

    val quizQuestions = listOf(
        Triple(
            "ما هو اسم العملاق الخاص بـ إرين ييغر؟",
            listOf("العملاق المدرع", "العملاق المهاجم", "العملاق الضخم", "عملاق الفك"),
            1
        ),
        Triple(
            "من هو قائد فيلق الاستطلاع الأسطوري الذي هزم العملاق القرد؟",
            listOf("إروين سميث", "ليفاي أكرمان", "هانجي زوي", "جان كيرشتاين"),
            1
        ),
        Triple(
            "ما هي أعلى رتبة سحرية في أكاديمية جوجوتسو كايسن؟",
            listOf("الرتبة الأولى", "الرتبة الخاصة (Special Grade)", "الرتبة الثانية", "المعالج الأعظم"),
            1
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("economy_game_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (onClose != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نادي ألعاب وجوائز الأوتاكو",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
        // Economy Balance Header Cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                border = BorderStroke(1.dp, AnimeBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اقتصاد ورصيد الأوتاكو",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AnimeTextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AnimeNeonPurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "المستوى ${economy.level}",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = AnimeNeonPurple
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Coins
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("الكوينز", fontSize = 10.sp, color = AnimeTextMuted)
                                Text("${economy.coins}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = AnimeTextPrimary)
                            }
                        }

                        // Gems
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("الجواهر", fontSize = 10.sp, color = AnimeTextMuted)
                                Text("${economy.gems}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = AnimeTextPrimary)
                            }
                        }

                        // XP
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AnimeSuccessGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("الخبرة XP", fontSize = 10.sp, color = AnimeTextMuted)
                                Text("${economy.xp} / 300", fontWeight = FontWeight.Black, fontSize = 16.sp, color = AnimeTextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (economy.xp % 300) / 300f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AnimeNeonPurple,
                        trackColor = AnimeCardSurfaceHover
                    )
                }
            }
        }

        // Daily Reward Chest
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClaimDailyReward()
                        Toast.makeText(context, "تم استلام مكافأة الحضور اليومي: +150 كوينز و +10 جواهر!", Toast.LENGTH_SHORT).show()
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                border = BorderStroke(1.5.dp, AnimeGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(AnimeGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = AnimeGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "صندوق الحضور اليومي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AnimeTextPrimary
                            )
                            Text(
                                text = "سلسلة الحضور: ${economy.dailyStreak} أيام متتالية! اضغط للاستلام",
                                fontSize = 11.sp,
                                color = AnimeGold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onClaimDailyReward()
                            Toast.makeText(context, "تم استلام مكافأة اليوم!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("استلام", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        // Anime Quiz Battles (Mini-Game)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                border = BorderStroke(1.dp, AnimeNeonPurple.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Quiz, contentDescription = null, tint = AnimeNeonPurple, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "معركة كويز الأنمي 1v1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AnimeTextPrimary
                            )
                        }

                        Text(
                            text = "النقاط: $quizScore",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AnimeCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!quizFinished) {
                        val currentQ = quizQuestions[quizQuestionIndex]

                        Text(
                            text = "السؤال ${quizQuestionIndex + 1}: ${currentQ.first}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AnimeTextPrimary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        currentQ.second.forEachIndexed { idx, option ->
                            val isSelected = selectedAnswerIndex == idx
                            val isCorrect = idx == currentQ.third
                            val bgColor = when {
                                selectedAnswerIndex == -1 -> AnimeCardSurfaceHover
                                isSelected && isCorrect -> AnimeSuccessGreen.copy(alpha = 0.4f)
                                isSelected && !isCorrect -> AnimeCrimson.copy(alpha = 0.4f)
                                isCorrect -> AnimeSuccessGreen.copy(alpha = 0.3f)
                                else -> AnimeCardSurfaceHover
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bgColor)
                                    .border(1.dp, if (isSelected) AnimeNeonPurple else AnimeBorder, RoundedCornerShape(10.dp))
                                    .clickable(enabled = selectedAnswerIndex == -1) {
                                        selectedAnswerIndex = idx
                                        if (isCorrect) {
                                            quizScore += 100
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }

                        if (selectedAnswerIndex != -1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (quizQuestionIndex + 1 < quizQuestions.size) {
                                        quizQuestionIndex += 1
                                        selectedAnswerIndex = -1
                                    } else {
                                        quizFinished = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeNeonPurple)
                            ) {
                                Text(
                                    text = if (quizQuestionIndex + 1 < quizQuestions.size) "السؤال التالي" else "إنهاء الكويز واستلام الجوائز",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Quiz Results
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AnimeGold,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("انتهت المعركة بنجاح!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AnimeTextPrimary)
                            Text("مجموع نقاطك: $quizScore نقطة (+200 كوينز مكافأة)", fontSize = 13.sp, color = AnimeGold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    quizFinished = false
                                    quizQuestionIndex = 0
                                    selectedAnswerIndex = -1
                                    quizScore = 0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeNeonPurple)
                            ) {
                                Text("لعب جولة جديدة", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Digital Collectibles & Character Cards
        item {
            Text(
                text = "بطاقات شخصيات الأنمي النادرة",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = AnimeTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            val cards = listOf(
                Triple("ليفاي أكرمان", "أسطوري ★★★", R.drawable.anime_character_art_1791388984389),
                Triple("غوجو ساتورو", "أسطوري ★★★", R.drawable.anime_manga_art_1791388998934),
                Triple("سون غوكو", "نادر جداً ★★", R.drawable.black_anime_banner_1791388840143)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards) { (name, rarity, imageRes) ->
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .height(210.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                        border = BorderStroke(1.5.dp, AnimeGold)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = rarity,
                                    fontSize = 10.sp,
                                    color = AnimeGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
