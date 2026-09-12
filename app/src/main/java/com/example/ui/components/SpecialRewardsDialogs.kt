package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.GameTheme
import com.example.ui.theme.GameThemes
import kotlin.random.Random

// --- CONFETTI & PARTICLES OVERLAY ---
@Composable
fun ConfettiOverlay(modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    val particleCount = 45
    val colors = listOf(
        Color(0xFFFFD700), Color(0xFFFF4081), Color(0xFF00E676),
        Color(0xFF00B0FF), Color(0xFFFF9100), Color(0xFFE040FB)
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val currentProgress = progress.value
        val random = Random(42)

        for (i in 0 until particleCount) {
            val startX = random.nextFloat() * size.width
            val speed = 0.6f + random.nextFloat() * 0.8f
            val currentY = (currentProgress * size.height * speed) % size.height
            val particleColor = colors[i % colors.size]
            val particleSize = (6..14).random().dp.toPx()

            drawCircle(
                color = particleColor.copy(alpha = (1f - currentProgress * 0.3f).coerceIn(0f, 1f)),
                radius = particleSize / 2,
                center = Offset(startX + (currentProgress * 20f * (if (i % 2 == 0) 1 else -1)), currentY)
            )
        }
    }
}

// --- PIGGY BANK DIALOG ---
@Composable
fun PiggyBankDialog(
    isOpen: Boolean,
    theme: GameTheme,
    piggyCoins: Int,
    isVip: Boolean,
    onClaimCoins: () -> Unit,
    onWatchAdToBreak: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val targetCapacity = 100
    val progress = (piggyCoins.toFloat() / targetCapacity).coerceIn(0f, 1f)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(24.dp)),
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
                    Text(
                        text = "🐖 قلک جادویی کلمات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textColor
                    )
                    Spacer(modifier = Modifier.width(36.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Piggy Icon with Glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFFF8E1), Color(0xFFFFE082), Color(0xFFFFB300))
                            )
                        )
                        .border(3.dp, Color(0xFFFFD54F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐖", fontSize = 54.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "سکه ذخیره شده: $piggyCoins از $targetCapacity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFFFF8F00),
                    trackColor = Color(0xFFFFECB3),
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "با پیدا کردن کلمات اضافی در مراحل، سکه‌ها درون قلک جمع می‌شوند!",
                    fontSize = 12.sp,
                    color = theme.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isVip) {
                    Button(
                        onClick = onClaimCoins,
                        enabled = piggyCoins > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_piggy_vip_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "شکستن قلک و دریافت $piggyCoins سکه (ویژه VIP)",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onWatchAdToBreak,
                            enabled = piggyCoins >= 15,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("claim_piggy_ad_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.PlayCircleOutline, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "شکستن قلک با ویدیوی جایزه‌ای",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (piggyCoins >= 50) {
                            FilledTonalButton(
                                onClick = onClaimCoins,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("دریافت عادی بدون تبلیغ ($piggyCoins سکه)")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- STAR CHEST REWARD DIALOG ---
@Composable
fun StarChestDialog(
    isOpen: Boolean,
    theme: GameTheme,
    starProgress: Int,
    maxStars: Int = 10,
    onOpenChest: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val isReady = starProgress >= maxStars

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFF7E57C2), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.textColor)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9), Color(0xFF7E57C2))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isReady) "🎁" else "📦", fontSize = 50.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "صندوقچه ستاره‌های جادویی",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "پیشرفت ستاره‌ها: $starProgress از $maxStars ⭐",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5E35B1)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { (starProgress.toFloat() / maxStars).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFF7E57C2),
                    trackColor = Color(0xFFD1C4E9),
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "با برنده شدن در هر مرحله ۳ ستاره دریافت می‌کنید. وقتی صندوق پر شود، جایزه بزرگ ۱۰۰ سکه و کارت راهنما آزاد می‌شود!",
                    fontSize = 12.sp,
                    color = theme.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onOpenChest,
                    enabled = isReady,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_star_chest_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isReady) "🎉 باز کردن صندوق و دریافت جایزه!" else "هنوز ستاره کافی ندارید",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- THEME SELECTOR DIALOG ---
@Composable
fun ThemeSelectorDialog(
    isOpen: Boolean,
    currentThemeId: String,
    isVip: Boolean,
    onSelectTheme: (String) -> Unit,
    onOpenVipPurchase: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val currentTheme = GameThemes.getThemeById(currentThemeId)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(2.dp, currentTheme.primaryVariant, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = currentTheme.cardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن", tint = currentTheme.textColor)
                    }
                    Text(
                        text = "🎨 گالری تم‌های اصیل ایرانی",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.textColor
                    )
                    Spacer(modifier = Modifier.width(36.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GameThemes.allThemes.forEach { theme ->
                        val isSelected = theme.id == currentThemeId
                        val isLocked = theme.id == "royal" && !isVip

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = theme.cardBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) theme.accent else theme.cardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isLocked) {
                                        onOpenVipPurchase()
                                    } else {
                                        onSelectTheme(theme.id)
                                    }
                                }
                                .testTag("theme_item_${theme.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Palette preview circle
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.sweepGradient(
                                                    listOf(theme.primary, theme.secondary, theme.accent, theme.primary)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = theme.icon, fontSize = 18.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = theme.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = theme.textColor
                                        )
                                        Text(
                                            text = theme.subtitle,
                                            fontSize = 11.sp,
                                            color = theme.textSecondary
                                        )
                                    }
                                }

                                when {
                                    isLocked -> {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFFFD700)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Lock,
                                                    contentDescription = "قفل VIP",
                                                    tint = Color(0xFF5D3E00),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "VIP",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF5D3E00)
                                                )
                                            }
                                        }
                                    }
                                    isSelected -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "انتخاب شده",
                                            tint = theme.accent,
                                            modifier = Modifier.size(24.dp)
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
}
