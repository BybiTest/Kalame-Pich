package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.GameTheme

@Composable
fun DailyChallengeDialog(
    isOpen: Boolean,
    theme: GameTheme,
    dailyStreak: Int,
    isCompletedToday: Boolean,
    onCompleteDaily: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    // Daily mystery word challenge
    val targetWord = "دانشگاه"
    val hint = "محل آموزش عالی و تحصیل علم و فرهنگ"
    val challengeLetters = listOf('د', 'ا', 'ن', 'ش', 'گ', 'ا', 'ه')

    var enteredLetters by remember { mutableStateOf(listOf<Char>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(isCompletedToday) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFFF9800), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.textColor)
                    }

                    // Streak Flame Badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFE0B2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "استریک روزانه",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "روز $dailyStreak متوالی",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBF360C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(36.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "🔥 معمای زرین روز",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.textColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "راهنما: $hint",
                    fontSize = 13.sp,
                    color = theme.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Boxes for Target Word (7 Letters)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in targetWord.indices) {
                        val char = enteredLetters.getOrNull(i)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (char != null) theme.primaryVariant else theme.letterWheelCenter
                                )
                                .border(
                                    1.dp,
                                    if (char != null) Color(0xFFFFB300) else theme.cardBorder,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char?.toString() ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (char != null) Color.White else theme.textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isSuccess) {
                    // Letter buttons to select
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        challengeLetters.forEachIndexed { index, char ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFFFFB74D), Color(0xFFFF9800))
                                        )
                                    )
                                    .clickable {
                                        if (enteredLetters.size < targetWord.length) {
                                            enteredLetters = enteredLetters + char
                                        }
                                    }
                                    .testTag("daily_letter_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.toString(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val current = enteredLetters.joinToString("")
                                if (current == targetWord) {
                                    isSuccess = true
                                    onCompleteDaily()
                                } else {
                                    errorMessage = "کلمه اشتباه است! دوباره تلاش کنید."
                                    enteredLetters = emptyList()
                                }
                            },
                            enabled = enteredLetters.size == targetWord.length,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("submit_daily_word_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ثبت پاسخ", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { enteredLetters = emptyList(); errorMessage = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("پاک کردن")
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉 معما حل شد!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "جایزه: ۷۰ سکه طلا + ۳۰ امتیاز XP دریافت شد.",
                                fontSize = 13.sp,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}
