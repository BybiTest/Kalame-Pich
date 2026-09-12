package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserEntity
import com.example.ui.theme.GameTheme
import com.example.ui.theme.GameThemes

@Composable
fun ProfileScreen(
    user: UserEntity?,
    theme: GameTheme,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onOpenDevGuide: () -> Unit,
    onOpenThemeSelector: () -> Unit,
    onOpenLuckyWheel: () -> Unit,
    onOpenPiggyBank: () -> Unit,
    onOpenTapsellGateway: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userXp = user?.xp ?: 0
    val userLevel = (userXp / 100) + 1
    val currentLevelXp = userXp % 100
    val leagueRank = when {
        userLevel >= 12 -> "شهریار واژگان 👑"
        userLevel >= 7 -> "استاد ادب و فرهنگ 🥇"
        userLevel >= 4 -> "سخن‌سنج ماهر 🥈"
        else -> "شاگرد نوپا 🥉"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User Profile & League Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                if (user?.isVip == true)
                                    Brush.radialGradient(listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFFB300)))
                                else
                                    Brush.radialGradient(listOf(theme.primary.copy(alpha = 0.3f), theme.primaryVariant))
                            )
                            .border(2.5.dp, if (user?.isVip == true) Color(0xFFFFD700) else theme.accent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "آواتار ویژه بازی",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "بازیکن کلمات",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = theme.textColor
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFE0B2)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${user?.dailyStreak ?: 1} روز",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBF360C)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "رتبه لیگ: $leagueRank (سطح $userLevel)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = theme.primaryVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (user?.isVip == true) Color(0xFFFFF8E1) else theme.letterWheelCenter
                        ) {
                            Text(
                                text = if (user?.isVip == true) "عضو طلایی VIP (${user.vipPlanName})" else "کاربر عادی (نسخه رایگان)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (user?.isVip == true) Color(0xFFE65100) else theme.textSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // XP Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "پیشرفت سطح: $currentLevelXp / 100 XP", fontSize = 12.sp, color = theme.textSecondary)
                    Text(text = "سطح $userLevel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { currentLevelXp / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = theme.accent,
                    trackColor = theme.letterWheelCenter,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Themes & Customization Entry Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.accent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenThemeSelector() }
                .testTag("open_theme_selector_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(theme.letterButtonGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "گالری تم‌های گرافیکی",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = theme.textColor
                        )
                        Text(
                            text = "تم فعال: ${theme.name} ${theme.icon}",
                            fontSize = 12.sp,
                            color = theme.textSecondary
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = theme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Special 3D Game Artwork Showcase Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("special_game_artwork_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تصویر اختصاصی بازی",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = theme.textColor
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFB300).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = "گرافیک سه‌بعدی و پوستر ویژه ⭐",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8F00),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, theme.accent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_game_special),
                        contentDescription = "تصویر ویژه و پوستر رسمی بازی کلمات و جدول",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xAA0B132B)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "دنیای سحرآمیز واژگان و جدول‌های ذهنی",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "طراحی اختصاصی با هوش مصنوعی و گرافیک سه‌بعدی",
                                color = Color(0xFFECEFF1),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Rewards (Wheel & Piggy Bank) Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = theme.cardBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenLuckyWheel() }
                    .testTag("profile_lucky_wheel_card")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎡", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "گردونه شانس", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.textColor)
                    Text(text = "جوایز روزانه", fontSize = 11.sp, color = theme.textSecondary)
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = theme.cardBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF8F00)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenPiggyBank() }
                    .testTag("profile_piggy_bank_card")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🐖", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "قلک کلمات", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.textColor)
                    Text(text = "${user?.piggyBankCoins ?: 0} سکه", fontSize = 11.sp, color = Color(0xFFE65100))
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Stats Section
        Text(
            text = "آمار و دستاوردهای شما",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = theme.textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        val stats = listOf(
            Triple("مراحل کلمات تکمیل شده", "${user?.levelsCompleted ?: 0}", Icons.Default.EmojiEvents),
            Triple("جداول متقاطع کامل شده", "${user?.crosswordsCompleted ?: 0}", Icons.Default.Extension),
            Triple("کلمات امتیازی کشف شده", "${user?.bonusWordsCount ?: 0}", Icons.Default.Star),
            Triple("تبلیغات تماشا شده", "${user?.adsWatchedCount ?: 0}", Icons.Default.PlayCircle),
            Triple("کل سکه‌های کیف پول", "${user?.coins ?: 0}", Icons.Default.MonetizationOn)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                stats.forEachIndexed { index, (label, value, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = theme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = label, fontSize = 13.sp, color = theme.textColor)
                        }

                        Text(
                            text = value,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = theme.primaryVariant
                        )
                    }
                    if (index < stats.size - 1) {
                        HorizontalDivider(
                            color = theme.cardBorder.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Developer Credit & About Section - سیدحمیدموسوی زاده
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF64B5F6)),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { onOpenAbout() }
                .testTag("about_developer_profile_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "سازنده و توسعه‌دهنده:",
                            fontSize = 11.sp,
                            color = Color(0xFF546E7A)
                        )
                        Text(
                            text = "سیدحمیدموسوی زاده",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Text(
                        text = "درباره ما",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Settings Toggles
        Text(
            text = "تنظیمات بازی",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = theme.textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = theme.textSecondary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("جلوه‌های صوتی بازی", fontSize = 13.sp, color = theme.textColor)
                    }
                    Switch(
                        checked = user?.soundEnabled ?: true,
                        onCheckedChange = { onToggleSound() }
                    )
                }

                HorizontalDivider(
                    color = theme.cardBorder.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = theme.textSecondary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("لرزش (هپتیک لمسی)", fontSize = 13.sp, color = theme.textColor)
                    }
                    Switch(
                        checked = user?.hapticsEnabled ?: true,
                        onCheckedChange = { onToggleHaptics() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tapsell Ad Gateway Button
        Button(
            onClick = onOpenTapsellGateway,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("open_tapsell_gateway_profile_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0081CB)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "درگاه تبلیغات تپسل (tapsell.ir) و تنظیمات",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Developer Monetization Architecture Button
        Button(
            onClick = onOpenDevGuide,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("open_dev_guide_bottom_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = theme.primaryVariant),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Code, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "راهنمای انتشار در بازار، تپسل و ادی موبی",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
