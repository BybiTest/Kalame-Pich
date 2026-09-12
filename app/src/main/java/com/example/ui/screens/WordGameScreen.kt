package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameLevelsData
import com.example.data.UserEntity
import com.example.ui.WordGameState
import com.example.ui.components.ConfettiOverlay
import com.example.ui.theme.GameTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun WordGameScreen(
    state: WordGameState,
    user: UserEntity?,
    theme: GameTheme,
    onSelectLetter: (Char) -> Unit,
    onRemoveLastLetter: () -> Unit,
    onClearLetters: () -> Unit,
    onSubmitWord: () -> Unit,
    onShuffleLetters: () -> Unit,
    onUseHint: () -> Unit,
    onNextLevel: () -> Unit,
    onDismissWinDialog: () -> Unit,
    onOpenPiggyBank: () -> Unit,
    onOpenStarChest: () -> Unit,
    onOpenDailyChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level = GameLevelsData.wordLevels.getOrElse(state.currentLevelIndex) {
        GameLevelsData.wordLevels.first()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.backgroundGradient)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gamification Quick Activity Row (Piggy Bank, Star Chest, Daily Streak)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Piggy Bank badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF8E1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenPiggyBank() }
                        .testTag("piggy_bank_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🐖", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.piggyBankCoins ?: 0}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                // Star Mystery Chest badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEDE7F6),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB39DDB)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenStarChest() }
                        .testTag("star_chest_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎁", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.starChestProgress ?: 0}/10 ⭐",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF5E35B1)
                        )
                    }
                }

                // Daily Challenge Flame badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFE0B2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenDailyChallenge() }
                        .testTag("daily_challenge_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "چالش روزانه",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "روز ${user?.dailyStreak ?: 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFBF360C)
                        )
                    }
                }
            }

            // Special 3D Game Artwork Hero Banner
            Card(
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .padding(bottom = 10.dp)
                    .testTag("special_game_hero_banner")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_game_special),
                        contentDescription = "تصویر زمینه اختصاصی بازی کلمات و جدول",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay for readability and aesthetic richness
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xCC0B132B),
                                        Color(0x770B132B),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFB300).copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "⭐ آرت‌ورک اختصاصی",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "بازی کلمات و جدول فکری",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "حدس کلمات طلایی با جوایز ویژه",
                                fontSize = 11.sp,
                                color = Color(0xFFE0E0E0)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0x33FFFFFF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x88FFFFFF)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "✨", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Level Title & Persian Theme Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.primaryVariant.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = "${level.title} • موضوع: ${level.theme}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.accent.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.accent.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "${state.foundWords.size} از ${level.targetWords.size} کلمه",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target Words Display Slots (Grid of wooden/ceramic letter tiles)
            Card(
                colors = CardDefaults.cardColors(containerColor = theme.cardBackground.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    level.targetWords.forEach { targetWord ->
                        val isFound = state.foundWords.contains(targetWord)
                        val revealedIndices = state.revealedLettersMap[targetWord] ?: emptySet()

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            targetWord.forEachIndexed { index, char ->
                                val isCharRevealed = isFound || revealedIndices.contains(index)
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .shadow(if (isFound) 3.dp else 1.dp, RoundedCornerShape(8.dp))
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isFound) Color(0xFF2E7D32)
                                            else if (revealedIndices.contains(index)) Color(0xFFFFB300)
                                            else theme.letterWheelCenter
                                        )
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isFound) Color(0xFF81C784)
                                            else if (revealedIndices.contains(index)) Color(0xFFFFE082)
                                            else theme.cardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isCharRevealed) char.toString() else "",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFound || revealedIndices.contains(index)) Color.White else theme.textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Feedback Notification Message
            AnimatedVisibility(
                visible = state.feedbackMessage != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                state.feedbackMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (msg.contains("آفرین") || msg.contains("امتیازی")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (msg.contains("آفرین") || msg.contains("امتیازی")) Color(0xFF81C784) else Color(0xFFE57373)
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = msg,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (msg.contains("آفرین") || msg.contains("امتیازی")) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Active Word Construction Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = theme.cardBackground,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primaryVariant.copy(alpha = 0.6f)),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onRemoveLastLetter,
                        enabled = state.selectedLetters.isNotEmpty(),
                        modifier = Modifier.testTag("word_backspace_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "حذف حرف",
                            tint = if (state.selectedLetters.isNotEmpty()) theme.primary else Color.Gray
                        )
                    }

                    Text(
                        text = if (state.selectedLetters.isNotEmpty()) state.selectedLetters.joinToString("") else "حروف زیر را برای ساخت کلمه لمس کنید",
                        fontSize = if (state.selectedLetters.isNotEmpty()) 22.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.selectedLetters.isNotEmpty()) theme.accent else theme.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = onSubmitWord,
                        enabled = state.selectedLetters.isNotEmpty(),
                        modifier = Modifier.testTag("word_submit_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "ثبت کلمه",
                            tint = if (state.selectedLetters.isNotEmpty()) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CIRCULAR LETTER WHEEL (Persian Arabesque Wheel Layout)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Background decorative circle and ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f - 20.dp.toPx()

                    // Glow circle
                    drawCircle(
                        color = theme.primary.copy(alpha = 0.12f),
                        radius = radius + 15.dp.toPx(),
                        center = center
                    )

                    // Outer orbital ring
                    drawCircle(
                        color = theme.primaryVariant.copy(alpha = 0.35f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Center Shuffle / Mandallion Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(theme.letterWheelCenter, theme.cardBackground)
                            )
                        )
                        .border(2.dp, theme.primaryVariant, CircleShape)
                        .clickable { onShuffleLetters() }
                        .testTag("wheel_center_shuffle_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "بر زدن حروف",
                        tint = theme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Circular arrangement of letters
                val letterCount = state.shuffledLetters.size
                val wheelRadiusDp = 86.dp

                state.shuffledLetters.forEachIndexed { index, char ->
                    val angleRad = (2 * PI * index / letterCount) - (PI / 2)
                    val offsetX = (wheelRadiusDp.value * cos(angleRad)).roundToInt().dp
                    val offsetY = (wheelRadiusDp.value * sin(angleRad)).roundToInt().dp

                    Box(
                        modifier = Modifier
                            .offset(offsetX, offsetY)
                            .size(54.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(theme.letterButtonGradient))
                            .border(2.dp, theme.accent, CircleShape)
                            .clickable { onSelectLetter(char) }
                            .testTag("letter_btn_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Control Bar (Hint, Clear, Shuffle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hint Button
                Button(
                    onClick = onUseHint,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primaryVariant),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("hint_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "راهنما",
                        tint = if (user?.isVip == true) Color(0xFFFFD700) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (user?.isVip == true) "راهنمای VIP" else "راهنما (۲۰ سکه)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Clear Button
                OutlinedButton(
                    onClick = onClearLetters,
                    enabled = state.selectedLetters.isNotEmpty(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("clear_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "پاک کردن",
                        tint = theme.textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("پاک", fontSize = 12.sp, color = theme.textColor)
                }
            }
        }

        // Confetti Celebration Overlay on Win
        if (state.showWinDialog) {
            ConfettiOverlay()

            AlertDialog(
                onDismissRequest = onDismissWinDialog,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🎉 آفرین! مرحله فتح شد!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "تمام کلمات این مرحله با موفقیت کشف شدند!",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Rewards breakdown
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFFF8E1),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (user?.isVip == true)
                                        "پاداش طلایی VIP: +${level.coinReward * 2} سکه (۲ برابر!)"
                                    else
                                        "پاداش مرحله: +${level.coinReward} سکه",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+۳ ستاره برای صندوقچه جادویی ⭐ | +۲۵ امتیاز تجربه XP ⚡",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDismissWinDialog()
                            onNextLevel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("win_next_level_btn")
                    ) {
                        Text("مرحله بعدی", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                    }
                }
            )
        }
    }
}
