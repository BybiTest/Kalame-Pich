package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.tapsell.TapsellDisplayMode
import com.example.data.tapsell.TapsellGatewayConfig

@Composable
fun TapsellGatewayDialog(
    isOpen: Boolean,
    config: TapsellGatewayConfig,
    isPinging: Boolean,
    onPingServer: () -> Unit,
    onSaveConfig: (TapsellGatewayConfig) -> Unit,
    onTestWatchAd: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current

    var appKeyInput by remember(config.appKey) { mutableStateOf(config.appKey) }
    var rewardedZoneInput by remember(config.rewardedZoneId) { mutableStateOf(config.rewardedZoneId) }
    var bannerZoneInput by remember(config.bannerZoneId) { mutableStateOf(config.bannerZoneId) }
    var displayMode by remember(config.displayMode) { mutableStateOf(config.displayMode) }
    var showSavedMessage by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .testTag("tapsell_gateway_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Tapsell Official Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0081CB), Color(0xFF00B0FF), Color(0xFF0091EA))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "T",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        color = Color(0xFF0081CB)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "درگاه تبلیغات تپسل (Tapsell)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "اتصال مستقیم به سامانه تبلیغات tapsell.ir",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    // Server Connection & Ping Status Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (config.isLiveConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (config.isLiveConnected) Color(0xFF81C784) else Color(0xFFE57373)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (config.isLiveConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = if (config.isLiveConnected) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (config.isLiveConnected) "متصل به سرور tapsell.ir" else "عدم دسترسی به سرور",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (config.isLiveConnected) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                    )
                                    Text(
                                        text = if (config.isLiveConnected) "پینگ پاسخگویی: ${config.lastPingMs} میلی‌ثانیه" else "لطفاً اینترنت خود را بررسی کنید",
                                        fontSize = 11.sp,
                                        color = if (config.isLiveConnected) Color(0xFF388E3C) else Color(0xFFC62828)
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onPingServer,
                                enabled = !isPinging,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("ping_tapsell_btn")
                            ) {
                                if (isPinging) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF0081CB)
                                    )
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تست پینگ", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Links to Tapsell Official Website & Developer Dashboard
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE1F5FE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81D4FA)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(config.serverUrl)))
                                    } catch (_: Exception) {}
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF0277BD), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("سایت تپسل (tapsell.ir)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0277BD))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEDE7F6),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB39DDB)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(config.dashboardUrl)))
                                    } catch (_: Exception) {}
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color(0xFF512DA8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("پنل ناشران تپسل", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF512DA8))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ad Display Mode Switcher
                    Text(
                        text = "نحوه نمایش تبلیغات تپسل در اپلیکیشن:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (displayMode == TapsellDisplayMode.INTERACTIVE_MEDIA) Color(0xFFE1F5FE) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (displayMode == TapsellDisplayMode.INTERACTIVE_MEDIA) Color(0xFF0081CB) else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { displayMode = TapsellDisplayMode.INTERACTIVE_MEDIA }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = if (displayMode == TapsellDisplayMode.INTERACTIVE_MEDIA) Color(0xFF0081CB) else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "پلیر چندرسانه‌ای",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (displayMode == TapsellDisplayMode.INTERACTIVE_MEDIA) Color(0xFF0081CB) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(text = "پوستر و جزئیات کمپین", fontSize = 9.sp, color = Color.Gray)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (displayMode == TapsellDisplayMode.LIVE_WEBVIEW) Color(0xFFE1F5FE) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (displayMode == TapsellDisplayMode.LIVE_WEBVIEW) Color(0xFF0081CB) else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { displayMode = TapsellDisplayMode.LIVE_WEBVIEW }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Web,
                                    contentDescription = null,
                                    tint = if (displayMode == TapsellDisplayMode.LIVE_WEBVIEW) Color(0xFF0081CB) else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "صفحه وب زنده (WebView)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (displayMode == TapsellDisplayMode.LIVE_WEBVIEW) Color(0xFF0081CB) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(text = "بارگذاری زنده سایت تبلیغ", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gateway Configuration Fields
                    Text(
                        text = "تنظیمات کلیدها و جایگاه‌های تپسل:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = appKeyInput,
                        onValueChange = { appKeyInput = it },
                        label = { Text("کلید اپلیکیشن (AppKey)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tapsell_app_key_field")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rewardedZoneInput,
                        onValueChange = { rewardedZoneInput = it },
                        label = { Text("شناسه جایگاه ویدیوی جایزه‌ای (Rewarded Zone ID)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tapsell_rewarded_zone_field")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = bannerZoneInput,
                        onValueChange = { bannerZoneInput = it },
                        label = { Text("شناسه جایگاه بنر همسان (Native Banner Zone ID)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tapsell_banner_zone_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Save Feedback
                    AnimatedVisibility(visible = showSavedMessage) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تنظیمات درگاه تپسل با موفقیت ذخیره شد.", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Action Buttons: Save & Test Ad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val updated = config.copy(
                                    appKey = appKeyInput,
                                    rewardedZoneId = rewardedZoneInput,
                                    bannerZoneId = bannerZoneInput,
                                    displayMode = displayMode
                                )
                                onSaveConfig(updated)
                                showSavedMessage = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0081CB)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("save_tapsell_config_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ذخیره درگاه", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onTestWatchAd()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("test_tapsell_ad_btn")
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مشاهده تبلیغ واقعی", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
