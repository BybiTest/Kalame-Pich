package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserEntity
import com.example.ui.theme.GameTheme

@Composable
fun TopGameBar(
    user: UserEntity?,
    theme: GameTheme,
    onOpenStore: () -> Unit,
    onOpenLuckyWheel: () -> Unit,
    onOpenThemes: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenDevGuide: () -> Unit,
    onOpenAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wheel_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = theme.cardBackground,
        tonalElevation = 3.dp,
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Coins Counter Chip with (+) button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFFF8E1),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenStore() }
                    .testTag("coins_header_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "سکه",
                        tint = Color(0xFFFF8F00),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${user?.coins ?: 0}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .size(17.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB300)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "افزایش سکه",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Interactive Lucky Wheel Badge (Pulsing to draw engagement)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFFECB3),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB300)),
                modifier = Modifier
                    .scale(pulseScale)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenLuckyWheel() }
                    .testTag("top_bar_lucky_wheel_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "🎡", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "گردونه",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D3E00)
                    )
                }
            }

            // VIP Badge or Store Callout
            if (user?.isVip == true) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFD700),
                    modifier = Modifier.testTag("vip_header_badge")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "عضو طلایی VIP",
                            tint = Color(0xFF7B5200),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "طلایی VIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D3E00)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .clickable { onOpenStore() }
                        .testTag("get_vip_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "دریافت اشتراک VIP",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "VIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Quick Actions: Themes, Sound & Dev Guide
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Theme switcher icon
                IconButton(
                    onClick = onOpenThemes,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("theme_picker_top_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "تغییر تم گرافیکی",
                        tint = theme.primaryVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Sound toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("toggle_sound_btn")
                ) {
                    Icon(
                        imageVector = if (user?.soundEnabled != false) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "تنظیم صدا",
                        tint = theme.textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Developer Monetization Guide
                IconButton(
                    onClick = onOpenDevGuide,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("dev_guide_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "راهنمای درآمدزایی بازار و تپسل",
                        tint = theme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // About App & Developer Credit
                IconButton(
                    onClick = onOpenAbout,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("about_app_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "درباره سازنده و بازی",
                        tint = theme.primaryVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

