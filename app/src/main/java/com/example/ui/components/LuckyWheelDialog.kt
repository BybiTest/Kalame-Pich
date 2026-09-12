package com.example.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.GameTheme
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class WheelSlice(
    val title: String,
    val icon: String,
    val color: Color,
    val textColor: Color = Color.White,
    val coinReward: Int = 0,
    val isVipTrial: Boolean = false,
    val isJackpot: Boolean = false
)

object WheelRewards {
    val slices = listOf(
        WheelSlice(title = "۵۰ سکه", icon = "🪙", color = Color(0xFF00ACC1), coinReward = 50),
        WheelSlice(title = "VIP رایگان", icon = "👑", color = Color(0xFFFFB300), isVipTrial = true),
        WheelSlice(title = "۱۰۰ سکه", icon = "💰", color = Color(0xFF7E57C2), coinReward = 100),
        WheelSlice(title = "۲x راهنما", icon = "💡", color = Color(0xFF43A047), coinReward = 40),
        WheelSlice(title = "۲۵ سکه", icon = "🪙", color = Color(0xFF26A69A), coinReward = 25),
        WheelSlice(title = "جک‌پات ۳۰۰!", icon = "🏆", color = Color(0xFFE53935), coinReward = 300, isJackpot = true),
        WheelSlice(title = "۷۵ سکه", icon = "🪙", color = Color(0xFF5C6BC0), coinReward = 75),
        WheelSlice(title = "۱۵۰ سکه", icon = "⭐", color = Color(0xFFFF7043), coinReward = 150)
    )
}

@Composable
fun LuckyWheelDialog(
    isOpen: Boolean,
    theme: GameTheme,
    coins: Int,
    isVip: Boolean,
    onClaimReward: (WheelSlice) -> Unit,
    onWatchAdForSpin: () -> Unit,
    onSpinWithCoins: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var isSpinning by remember { mutableStateOf(false) }
    var wonSlice by remember { mutableStateOf<WheelSlice?>(null) }
    var currentRotation by remember { mutableFloatStateOf(0f) }
    val rotationAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val sliceCount = WheelRewards.slices.size
    val sliceAngle = 360f / sliceCount

    fun spinWheel() {
        if (isSpinning) return
        isSpinning = true
        wonSlice = null

        val winningIndex = (0 until sliceCount).random()
        // Target angle points to top (270 degrees)
        val targetSliceCenter = winningIndex * sliceAngle + (sliceAngle / 2)
        val fullRotations = 5 * 360f
        // Invert to match top pointer
        val targetDegree = fullRotations + (360f - targetSliceCenter) + 270f

        scope.launch {
            rotationAnim.snapTo(currentRotation % 360f)
            rotationAnim.animateTo(
                targetValue = currentRotation + targetDegree,
                animationSpec = tween(
                    durationMillis = 3800,
                    easing = CubicBezierEasing(0.12f, 0.8f, 0.32f, 1f)
                )
            )
            currentRotation = rotationAnim.value
            isSpinning = false
            val result = WheelRewards.slices[winningIndex]
            wonSlice = result
            onClaimReward(result)
        }
    }

    Dialog(
        onDismissRequest = { if (!isSpinning) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(2.dp, theme.primaryVariant, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSpinning,
                        modifier = Modifier.testTag("wheel_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = theme.textColor
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎡 گردونه شانس طلایی",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textColor
                        )
                        Text(
                            text = "هر روز یک شانس برنده شدن جوایز بزرگ!",
                            fontSize = 12.sp,
                            color = theme.textSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF8E1),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFF8F00),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$coins",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Wheel Box Container with Indicator Pointer
                Box(
                    modifier = Modifier.size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Golden Ring Glow
                    Box(
                        modifier = Modifier
                            .size(276.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFF795548))
                                )
                            )
                    )

                    // Canvas Wheel Rotating
                    Canvas(
                        modifier = Modifier
                            .size(260.dp)
                            .rotate(rotationAnim.value)
                            .testTag("wheel_canvas")
                    ) {
                        val canvasSize = size.minDimension
                        val radius = canvasSize / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        WheelRewards.slices.forEachIndexed { index, slice ->
                            val startAngle = index * sliceAngle
                            // Draw Slice Sector
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = sliceAngle,
                                useCenter = true,
                                size = Size(canvasSize, canvasSize),
                                topLeft = Offset(center.x - radius, center.y - radius)
                            )
                            // Draw Separator line
                            val rad = Math.toRadians(startAngle.toDouble())
                            val endX = center.x + (radius * cos(rad)).toFloat()
                            val endY = center.y + (radius * sin(rad)).toFloat()
                            drawLine(
                                color = Color.White.copy(alpha = 0.8f),
                                start = center,
                                end = Offset(endX, endY),
                                strokeWidth = 2.dp.toPx()
                            )
                        }

                        // Outer border stroke
                        drawCircle(
                            color = Color(0xFFFFD700),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    // Top Pointer Triangle
                    Canvas(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(28.dp, 36.dp)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path, color = Color(0xFFE53935))
                        drawPath(path, color = Color.White, style = Stroke(width = 2.dp.toPx()))
                    }

                    // Center Spin Hub Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFFEE58), Color(0xFFF57F17))
                                )
                            )
                            .border(3.dp, Color.White, CircleShape)
                            .clickable(enabled = !isSpinning) { spinWheel() }
                            .testTag("spin_wheel_center_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isSpinning) "..." else "بچرخان!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3E2723),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Result Banner
                wonSlice?.let { won ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉 تبریک! شما برنده شدید:",
                                fontSize = 13.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${won.icon} ${won.title}",
                                fontSize = 18.sp,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Action Buttons to Spin Again
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Watch Ad for Free Spin
                    Button(
                        onClick = {
                            if (!isSpinning) {
                                onWatchAdForSpin()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wheel_watch_ad_spin_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("چرخش با تبلیغ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Spin with 30 Coins
                    FilledTonalButton(
                        onClick = {
                            if (!isSpinning && coins >= 30) {
                                onSpinWithCoins()
                                spinWheel()
                            }
                        },
                        enabled = !isSpinning && coins >= 30,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wheel_coin_spin_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("۳۰ سکه", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
