package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserEntity
import com.example.data.tapsell.TapsellGatewayConfig
import com.example.ui.components.TapsellNativeBanner

@Composable
fun StoreScreen(
    user: UserEntity?,
    tapsellConfig: TapsellGatewayConfig = TapsellGatewayConfig(),
    onWatchAd: () -> Unit,
    onOpenTapsellGateway: () -> Unit,
    onPurchaseVip: (String, String) -> Unit,
    onPurchaseCoins: (String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // VIP Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFB300), Color(0xFFFF8F00), Color(0xFFF57C00))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "اشتراک طلایی VIP بازار",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (user?.isVip == true) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White
                            ) {
                                Text(
                                    text = "فعال است",
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "مزایای عضویت طلایی:\n" +
                                "✓ حذف کامل تبلیغات بنری و بین‌برگه‌ای\n" +
                                "✓ راهنماهای نامحدود و رایگان در تمام مراحل\n" +
                                "✓ دریافت ۲ برابر سکه برای تمام مراحل و جداول\n" +
                                "✓ دریافت ۲۰۰ سکه جایزه اولیه بلافاصله پس از خرید",
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TAPSELL AD GATEWAY SECTION
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FD)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF90CAF9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Tapsell Banner Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0081CB),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "T",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "درگاه تبلیغات تپسل (tapsell.ir)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0D47A1)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (tapsellConfig.isLiveConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = if (tapsellConfig.isLiveConnected) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (tapsellConfig.isLiveConnected) "متصل به سرور تپسل (${tapsellConfig.lastPingMs}ms)" else "در حال اتصال به tapsell.ir",
                                    fontSize = 10.sp,
                                    color = if (tapsellConfig.isLiveConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }

                    // Settings Button to configure zone and view server stats
                    IconButton(
                        onClick = onOpenTapsellGateway,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE1F5FE))
                            .testTag("open_tapsell_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "تنظیمات درگاه تپسل",
                            tint = Color(0xFF0277BD),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Watch Ad Action Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "سکه رایگان با تماشای تبلیغ تپسل",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "+۵۰ سکه هدیه • تبلیغات واقعی شبکه تپسل",
                                    fontSize = 11.sp,
                                    color = Color(0xFF388E3C)
                                )
                            }
                        }

                        Button(
                            onClick = onWatchAd,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("watch_ad_store_btn")
                        ) {
                            Text("تماشا", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // REAL TAPSELL NATIVE BANNER AD
        TapsellNativeBanner(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // VIP Subscription Plans
        Text(
            text = "پلن‌های اشتراک طلایی VIP کافه بازار",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        val vipPlans = listOf(
            Triple("اشتراک ۱ ماهه طلایی", "۴۹٬۰۰۰ تومان", "vip_monthly"),
            Triple("اشتراک ۳ ماهه ویژه (۳۳٪ تخفیف)", "۹۹٬۰۰۰ تومان", "vip_quarterly"),
            Triple("اشتراک دائمی مادام‌العمر", "۱۹۹٬۰۰۰ تومان", "vip_lifetime")
        )

        vipPlans.forEachIndexed { index, (title, price, id) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    if (index == 1) 1.5.dp else 1.dp,
                    if (index == 1) Color(0xFFFFB300) else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = price,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32),
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { onPurchaseVip(title, price) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (index == 1) Color(0xFFFF8F00) else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("buy_vip_btn_$id")
                    ) {
                        Text("خرید VIP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Coin Packs
        Text(
            text = "بسته‌های سکه بازی",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        val coinPacks = listOf(
            Triple("بسته ۱۰۰ سکه", 100, "۹٬۰۰۰ تومان"),
            Triple("بسته ۵۰۰ سکه (+۵۰ سکه هدیه)", 550, "۲۹٬۰۰۰ تومان"),
            Triple("بسته ۱۵۰۰ سکه طلایی (+۲۰۰ سکه هدیه)", 1700, "۵۹٬۰۰۰ تومان")
        )

        coinPacks.forEachIndexed { index, (title, coins, price) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = price, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32), fontSize = 13.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { onPurchaseCoins(title, coins, price) },
                        modifier = Modifier.testTag("buy_coins_btn_$coins")
                    ) {
                        Text("خرید بسته", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
