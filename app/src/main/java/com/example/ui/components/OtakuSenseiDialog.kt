package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

data class AiAnimeRecommendation(
    val title: String,
    val arabicTitle: String,
    val genre: String,
    val episodes: String,
    val rating: String,
    val summary: String,
    val whyToWatch: String
)

data class TriviaQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class SenseiChatMessage(
    val isUser: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun OtakuSenseiDialog(
    onDismissRequest: () -> Unit,
    onRewardEarned: (Long) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("المستشار الذكي", "توصيات الأنمي", "معركة المعرفة", "أقوال الأساطير")

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("otaku_sensei_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeDarkSurface),
            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(AnimeViolet.copy(alpha = 0.3f), AnimeCyan.copy(alpha = 0.2f))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(AnimeCyan.copy(alpha = 0.2f))
                                    .border(1.dp, AnimeCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AnimeCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "أوتاكو سينسي AI",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = AnimeTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AnimeCyan)
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "ONLINE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                                Text(
                                    text = "رفيق الأنمي الذكي - إجابات وتحليلات فورية فائقة السرعة",
                                    fontSize = 10.sp,
                                    color = AnimeTextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.testTag("close_sensei_dialog_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = AnimeTextSecondary)
                        }
                    }
                }

                // Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = AnimeCardSurface,
                    contentColor = AnimeCyan,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AnimeCyan,
                            height = 2.dp
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
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) AnimeCyan else AnimeTextSecondary
                                )
                            }
                        )
                    }
                }

                // Body content based on tab
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    when (selectedTab) {
                        0 -> SenseiChatSection()
                        1 -> SenseiRecommendationsSection()
                        2 -> SenseiTriviaSection(onRewardEarned = onRewardEarned)
                        3 -> SenseiQuotesSection()
                    }
                }
            }
        }
    }
}

@Composable
private fun SenseiChatSection() {
    val messages = remember {
        mutableStateListOf(
            SenseiChatMessage(
                isUser = false,
                message = "مرحباً بك يا بطل! أنا أوتاكو سينسي ⚡ اسألني عن أي أنمي، مقارنة قوى (Power Scaling)، توصية خاصة، أو تحليل لأي مشهد!"
            )
        )
    }
    var inputText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "من الأقوى: غوكو أم سايتاما؟",
        "اقترح لي أنمي غموض وذكاء زي ديث نوت",
        "ما هو أفضل ترتيب لمتابعة Fate؟",
        "شرح تقنية الإنفينيتي لساتورو غوجو"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.isUser) AnimeViolet.copy(alpha = 0.35f) else AnimeCardSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (msg.isUser) AnimeViolet.copy(alpha = 0.6f) else AnimeBorder
                        ),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (msg.isUser) Icons.Default.QuestionAnswer else Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (msg.isUser) AnimeViolet else AnimeCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (msg.isUser) "أنت" else "أوتاكو سينسي",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.isUser) AnimeViolet else AnimeCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg.message,
                                fontSize = 13.sp,
                                color = AnimeTextPrimary,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick suggestions
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(quickQuestions) { q ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AnimeCardSurfaceHover)
                        .border(1.dp, AnimeBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            messages.add(SenseiChatMessage(isUser = true, message = q))
                            val reply = answerSenseiQuestion(q)
                            messages.add(SenseiChatMessage(isUser = false, message = reply))
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = q, fontSize = 11.sp, color = AnimeCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("اكتب سؤالك لسينسي...", fontSize = 12.sp, color = AnimeTextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("sensei_chat_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCyan,
                    unfocusedBorderColor = AnimeBorder,
                    focusedTextColor = AnimeTextPrimary,
                    unfocusedTextColor = AnimeTextPrimary,
                    focusedContainerColor = AnimeCardSurface,
                    unfocusedContainerColor = AnimeCardSurface
                ),
                maxLines = 2
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val userPrompt = inputText.trim()
                        inputText = ""
                        messages.add(SenseiChatMessage(isUser = true, message = userPrompt))
                        val reply = answerSenseiQuestion(userPrompt)
                        messages.add(SenseiChatMessage(isUser = false, message = reply))
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AnimeCyan)
                    .testTag("sensei_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "إرسال",
                    tint = Color.Black
                )
            }
        }
    }
}

private fun answerSenseiQuestion(prompt: String): String {
    val p = prompt.lowercase()
    return when {
        p.contains("غوكو") || p.contains("سايتاما") ->
            "⚡ نقاش العمالقة الأبدي! سايتاما مصمم كشخصية (Gag Character) تكسر جميع حدود القوة بلكمة واحدة. أما غوكو فقدرته تكمن في كسر حدوده عبر Ultra Instinct والتدريب الإلهي. في سياق كتابة ون بانتش مان، سايتاما لا يُقهر باللكمات، لكن في كون دراغون بول فغوكو يمتلك سرعة وخبرة أبعاد كونية!"
        p.contains("ديث نوت") || p.contains("غموض") || p.contains("ذكاء") ->
            "🔥 إذا عشقت ديث نوت، فإليك التوصيات الذهبية التي ستذهلك:\n1. Monster (تحفة نفسية مرعبة مع يوهان ليبرت)\n2. Code Geass (صراع تكتيكي استراتيجي مع لولوش)\n3. Steins;Gate (أعظم حبكة سفر عبر الزمن وغموض)\n4. Psycho-Pass (عالم سوداوي ومطاردات فكرية)"
        p.contains("fate") || p.contains("فيت") ->
            "🗡️ أفضل ترتيب لمتابعة سلسلة Fate الأسطورية:\n1. Fate/Zero (البداية الإيبيكية مع حرب الكأس المقدسة الرابعة)\n2. Fate/stay night: Unlimited Blade Works (استوديو Ufotable)\n3. Fate/stay night: Heaven's Feel (ثلاثية الأفلام الخارقة)"
        p.contains("غوجو") || p.contains("إنفينيتي") || p.contains("جوجوتسو") ->
            "🌌 تقنية المالانهاية (Limitless) لغوجو ساتورو تعمل كمفارقة زينو الرياضية: كلما اقترب الهجوم منه، يتم تقسيم المسافة إلى ما لانهاية مما يجعله يبدو متوقفاً تماماً! وعند دمج الأحمر والأزرق ينشأ البنفسجي (Hollow Purple) الذي يمحو المادة في مساره!"
        p.contains("ون بيس") || p.contains("لوفي") ->
            "🏴‍☠️ ون بيس ليس مجرد قصة قراصنة، بل أضخم بناء عالمي في تاريخ الأنمي! كشف لوفي عن الغير 5 (Gear 5) وتحرير قوة جوي بوي (Nika) قلب موازين القوى في العالم وجعل آرك الإيغهيد في قمة الحماس!"
        else ->
            "✨ سؤال رائع في عالم الأنمي! في هذا العالم الشاسع، كل عمل يحمل فلسفة خاصة وقوة ملهمة. سواء كنت تبحث عن قتال أسطوري كشونين، أو عمق درامي نفسي، أو حبكة خيال علمي، بلاك انمي يجمع لك أفضل التحليلات والنقاشات بين الأوتاكو!"
    }
}

@Composable
private fun SenseiRecommendationsSection() {
    val recs = listOf(
        AiAnimeRecommendation(
            title = "Jujutsu Kaisen",
            arabicTitle = "جوجوتسو كايسن",
            genre = "أكشن خارق للطبيعة",
            episodes = "47 حلقة + فيلم",
            rating = "8.7 / 10",
            summary = "صراع السحرة واللعنات في طوكيو مع إثارة وقتالات إخراجية استثنائية من استوديو مابا.",
            whyToWatch = "آرك حادثة الشيبويا يعتبر من أعظم آركات الأكشن المعاصرة في تاريخ الشونين!"
        ),
        AiAnimeRecommendation(
            title = "Attack on Titan",
            arabicTitle = "هجوم العمالقة",
            genre = "دراما ملحمية / غموض",
            episodes = "89 حلقة",
            rating = "9.1 / 10",
            summary = "صراع البشرية داخل الأسوار ضد العمالقة، سرعان ما ينقلب إلى صراع أيديولوجي تاريخي.",
            whyToWatch = "حبكة خالية من الأخطاء مع مفاجآت تقلب الطاولة وموسيقى تصويرية للموسيقار ساوانو."
        ),
        AiAnimeRecommendation(
            title = "Chainsaw Man",
            arabicTitle = "رجل المنشار",
            genre = "رعب مظلم / حركة",
            episodes = "12 حلقة",
            rating = "8.5 / 10",
            summary = "دينجي الشاب الفقير يتحول إلى هجين منشار شيطاني وينضم لصائدي الشياطين.",
            whyToWatch = "إخراج سينمائي جريء وغير تقليدي مع شخصيات مجنونة وموسيقى رائعة."
        ),
        AiAnimeRecommendation(
            title = "Frieren: Beyond Journey's End",
            arabicTitle = "فريرين: ما بعد نهاية الرحلة",
            genre = "خيال فلسفي / مغامرة",
            episodes = "28 حلقة",
            rating = "9.3 / 10",
            summary = "إلف ساحرة تعيش لقرون وتكتشف قيمة الوقت والمشاعر الإنسانية بعد رحيل رفاقها الأبطال.",
            whyToWatch = "أعلى أنمي تقييماً في التاريخ الحديث، هادئ ومؤثر وقتالاته إبداعية ومتقنة."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(recs) { item ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                border = BorderStroke(1.dp, AnimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${item.arabicTitle} (${item.title})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnimeTextPrimary
                            )
                            Text(
                                text = "${item.genre} • ${item.episodes}",
                                fontSize = 11.sp,
                                color = AnimeCyan
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AnimeGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.rating,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnimeGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.summary,
                        fontSize = 12.sp,
                        color = AnimeTextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeViolet.copy(alpha = 0.15f))
                            .border(1.dp, AnimeViolet.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = AnimeViolet,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "سر التوصية: ${item.whyToWatch}",
                                fontSize = 11.sp,
                                color = AnimeTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SenseiTriviaSection(
    onRewardEarned: (Long) -> Unit
) {
    val questions = remember {
        listOf(
            TriviaQuestion(
                id = 1,
                question = "من هو مؤلف مانجا هجوم العمالقة (Attack on Titan)؟",
                options = listOf("هاجيمي إيساياما", "إييتشيرو أودا", "ماساشي كيشيموتو", "تايت كوبو"),
                correctIndex = 0,
                explanation = "هاجيمي إيساياما هو العقل المدبر وراء قصة وهندسة عوالم هجوم العمالقة!"
            ),
            TriviaQuestion(
                id = 2,
                question = "ما هي فاكهة الشيطان الحقيقية لمونكي دي لوفي في ون بيس؟",
                options = listOf("غومو غومو نو مي", "هيتو هيتو نو مي: نموذج نيكا", "ميرا ميرا نو مي", "غورا غورا نو مي"),
                correctIndex = 1,
                explanation = "فاكهة لوفي الحقيقية هي زون أسطورية: هيتو هيتو نو مي، نموذج إله الشمس نيكا!"
            ),
            TriviaQuestion(
                id = 3,
                question = "في جوجوتسو كايسن، ما اسم نطاق (Domain Expansion) ريومن سوكونا؟",
                options = listOf("الفراغ اللانهائي", "الضريح الخبيث (Malevolent Shrine)", "حديقة الظلال", "مقام الجحيم"),
                correctIndex = 1,
                explanation = "نطاق سوكونا هو الضريح الخبيث، وهو نطاق مفتوح بدون حاجز يقطع كل ما في محيط 200 متر!"
            ),
            TriviaQuestion(
                id = 4,
                question = "في أنمي هانتر x هانتر، من هو زعيم عصابة العناكب (Phantom Troupe)؟",
                options = listOf("هيسوكا", "إيلومي", "كورولو لوسيفر", "فيتان"),
                correctIndex = 2,
                explanation = "كورولو لوسيفر هو رأس العنكبوت وصاحب كتاب سر اللصوص!"
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }

    val currentQ = questions[currentIndex]

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "السؤال ${currentIndex + 1} من ${questions.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AnimeCyan
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = AnimeGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$score نقطة",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnimeGold
                )
            }
        }

        // Question Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
            border = BorderStroke(1.dp, AnimeBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = currentQ.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnimeTextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentQ.options.forEachIndexed { idx, opt ->
                val isSelected = selectedOption == idx
                val isCorrect = idx == currentQ.correctIndex

                val borderColor = when {
                    answered && isCorrect -> Color.Green
                    answered && isSelected && !isCorrect -> AnimeCrimson
                    isSelected -> AnimeCyan
                    else -> AnimeBorder
                }

                val bgColor = when {
                    answered && isCorrect -> Color.Green.copy(alpha = 0.15f)
                    answered && isSelected && !isCorrect -> AnimeCrimson.copy(alpha = 0.15f)
                    isSelected -> AnimeCyan.copy(alpha = 0.1f)
                    else -> AnimeCardSurface
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable(enabled = !answered) {
                            selectedOption = idx
                            answered = true
                            if (idx == currentQ.correctIndex) {
                                score += 25
                                onRewardEarned(25)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = opt,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = AnimeTextPrimary
                        )
                        if (answered && isCorrect) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Green)
                        }
                    }
                }
            }
        }

        // Explanation & Next Button
        AnimatedVisibility(visible = answered) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeViolet.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "الشرح: ${currentQ.explanation}",
                        fontSize = 11.sp,
                        color = AnimeTextSecondary,
                        lineHeight = 16.sp
                    )
                }

                Button(
                    onClick = {
                        if (currentIndex + 1 < questions.size) {
                            currentIndex++
                            answered = false
                            selectedOption = null
                        } else {
                            // Reset
                            currentIndex = 0
                            answered = false
                            selectedOption = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (currentIndex + 1 < questions.size) "السؤال التالي ➔" else "إعادة الاختبار 🔥",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun SenseiQuotesSection() {
    val quotes = listOf(
        Pair("لا تبكِ لأن الأمر انتهى، ابتسم لأنك قاتلت حتى النهاية بشرف.", "إروين سميث - هجوم العمالقة"),
        Pair("المعرفة والوعي ضبابيان، والواقع قد يكون مجرد وهم يراه الجميع كما يحبون.", "إيتاشي أوتشيها - ناروتو شيبودن"),
        Pair("إذا لم تخاطر بحياتك، فلن تتمكن من صنع مستقبل لك.", "مونكي دي لوفي - ون بيس"),
        Pair("لا أحد يعرف النتيجة مسبقاً، كل ما عليك فعله هو اختيار القرار الذي لن تندم عليه لاحقاً.", "ليفاي أكرمان - هجوم العمالقة"),
        Pair("العالم قاسٍ جداً، لكنه في الوقت ذاته جميل للغاية.", "ميكاسا أكرمان - هجوم العمالقة"),
        Pair("الاستيقاظ على الألم أفضل بكثير من النوم في وهم السعادة الزائفة.", "مادارا أوتشيها - ناروتو شيبودن")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(quotes) { (quote, author) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeCardSurface),
                border = BorderStroke(1.dp, AnimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "“$quote”",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AnimeTextPrimary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— $author",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeCyan,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
