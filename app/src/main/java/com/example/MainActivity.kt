package com.example

import android.app.Activity
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.monetization.TapsellAdManager
import com.example.ui.GameViewModel
import com.example.ui.components.BazaarPaymentDialog
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.DailyChallengeDialog
import com.example.ui.components.DeveloperGuideDialog
import com.example.ui.components.LuckyWheelDialog
import com.example.ui.components.PiggyBankDialog
import com.example.ui.components.StarChestDialog
import com.example.ui.components.TapsellGatewayDialog
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.components.TopGameBar
import com.example.ui.screens.AboutDialog
import com.example.ui.screens.CrosswordGameScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.screens.WordGameScreen
import com.example.ui.theme.GameThemes
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private var toneGenerator: ToneGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*
         * Initialize Tapsell Plus once when the Activity starts.
         *
         * The actual App Key is injected through BuildConfig.
         * It must NOT be hardcoded in this source file.
         */
        try {
            TapsellAdManager.getInstance().initialize(
                applicationContext,
                BuildConfig.TAPSELL_APP_KEY
            )
        } catch (_: Exception) {
            // Tapsell configuration errors must not crash the game.
        }

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (_: Exception) {}

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = GameRepository(database.gameDao())

        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(repository) as T
            }
        }

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {
                    val gameViewModel: GameViewModel =
                        viewModel(factory = viewModelFactory)

                    MainGameApp(
                        viewModel = gameViewModel,
                        activity = this@MainActivity,
                        onPlaySound = { soundType ->
                            playSound(soundType)
                        }
                    )
                }
            }
        }
    }

    private fun playSound(type: SoundType) {
        try {
            when (type) {
                SoundType.CLICK ->
                    toneGenerator?.startTone(
                        ToneGenerator.TONE_PROP_BEEP,
                        50
                    )

                SoundType.SUCCESS ->
                    toneGenerator?.startTone(
                        ToneGenerator.TONE_PROP_ACK,
                        150
                    )

                SoundType.ERROR ->
                    toneGenerator?.startTone(
                        ToneGenerator.TONE_PROP_NACK,
                        100
                    )
            }
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator?.release()
        toneGenerator = null
    }
}

enum class SoundType {
    CLICK,
    SUCCESS,
    ERROR
}

@Composable
fun MainGameApp(
    viewModel: GameViewModel,
    activity: Activity,
    onPlaySound: (SoundType) -> Unit
) {
    val user by viewModel.userProfile.collectAsStateWithLifecycle()
    val wordState by viewModel.wordState.collectAsStateWithLifecycle()
    val crosswordState by viewModel.crosswordState.collectAsStateWithLifecycle()
    val purchaseState by viewModel.purchaseState.collectAsStateWithLifecycle()
    val showDevGuide by viewModel.showDevMonetizationGuide.collectAsStateWithLifecycle()

    // Gamification states
    val showLuckyWheel by viewModel.showLuckyWheel.collectAsStateWithLifecycle()
    val showPiggyBank by viewModel.showPiggyBank.collectAsStateWithLifecycle()
    val showStarChest by viewModel.showStarChest.collectAsStateWithLifecycle()
    val showDailyChallenge by viewModel.showDailyChallenge.collectAsStateWithLifecycle()
    val showThemeSelector by viewModel.showThemeSelector.collectAsStateWithLifecycle()
    val showConfetti by viewModel.showConfetti.collectAsStateWithLifecycle()

    // Tapsell Gateway states
    val tapsellConfig by viewModel.tapsellConfig.collectAsStateWithLifecycle()
    val showTapsellGatewayDialog by
        viewModel.showTapsellGatewayDialog.collectAsStateWithLifecycle()
    val isPingingTapsell by
        viewModel.isPingingTapsell.collectAsStateWithLifecycle()

    val showAboutDialog by
        viewModel.showAboutDialog.collectAsStateWithLifecycle()

    val currentTheme =
        GameThemes.getThemeById(
            user?.selectedThemeId ?: "turquoise"
        )

    var currentTab by remember {
        mutableIntStateOf(0)
    }

    val haptic = LocalHapticFeedback.current

    // Auto-dismiss confetti after 3 seconds
    if (showConfetti) {
        LaunchedEffect(Unit) {
            delay(3000)
            viewModel.dismissConfetti()
        }
    }

    fun triggerFeedback(soundType: SoundType) {
        if (user?.soundEnabled != false) {
            onPlaySound(soundType)
        }

        if (user?.hapticsEnabled != false) {
            haptic.performHapticFeedback(
                HapticFeedbackType.LongPress
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        topBar = {
            TopGameBar(
                user = user,
                theme = currentTheme,
                onOpenStore = {
                    currentTab = 2
                },
                onOpenLuckyWheel = {
                    viewModel.toggleLuckyWheel(true)
                },
                onOpenThemes = {
                    viewModel.toggleThemeSelector(true)
                },
                onToggleSound = {
                    viewModel.toggleSound()
                },
                onOpenDevGuide = {
                    viewModel.toggleDevMonetizationGuide(true)
                },
                onOpenAbout = {
                    viewModel.toggleAboutDialog(true)
                }
            )
        },

        bottomBar = {
            NavigationBar(
                containerColor = currentTheme.cardBackground,
                modifier = Modifier.testTag(
                    "game_bottom_navigation"
                )
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = {
                        currentTab = 0
                        triggerFeedback(SoundType.CLICK)
                    },
                    icon = {
                        Icon(
                            Icons.Default.Extension,
                            contentDescription = "حدس کلمات"
                        )
                    },
                    label = {
                        Text("حدس کلمات")
                    },
                    modifier = Modifier.testTag(
                        "nav_tab_word_game"
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = {
                        currentTab = 1
                        triggerFeedback(SoundType.CLICK)
                    },
                    icon = {
                        Icon(
                            Icons.Default.GridOn,
                            contentDescription = "جدول متقاطع"
                        )
                    },
                    label = {
                        Text("جدول کلمات")
                    },
                    modifier = Modifier.testTag(
                        "nav_tab_crossword"
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = {
                        currentTab = 2
                        triggerFeedback(SoundType.CLICK)
                    },
                    icon = {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = "فروشگاه و VIP"
                        )
                    },
                    label = {
                        Text("فروشگاه و VIP")
                    },
                    modifier = Modifier.testTag(
                        "nav_tab_store"
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = {
                        currentTab = 3
                        triggerFeedback(SoundType.CLICK)
                    },
                    icon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "پروفایل و آمار"
                        )
                    },
                    label = {
                        Text("پروفایل و آمار")
                    },
                    modifier = Modifier.testTag(
                        "nav_tab_profile"
                    )
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (currentTab) {

                0 -> WordGameScreen(
                    state = wordState,
                    user = user,
                    theme = currentTheme,

                    onSelectLetter = { char ->
                        triggerFeedback(SoundType.CLICK)
                        viewModel.selectLetter(char)
                    },

                    onRemoveLastLetter = {
                        triggerFeedback(SoundType.CLICK)
                        viewModel.removeLastLetter()
                    },

                    onClearLetters = {
                        triggerFeedback(SoundType.CLICK)
                        viewModel.clearLetters()
                    },

                    onSubmitWord = {
                        viewModel.submitWord()
                        triggerFeedback(SoundType.SUCCESS)
                    },

                    onShuffleLetters = {
                        triggerFeedback(SoundType.CLICK)
                        viewModel.shuffleLetters()
                    },

                    onUseHint = {
                        triggerFeedback(SoundType.CLICK)
                        viewModel.useHint()
                    },

                    onNextLevel = {
                        triggerFeedback(SoundType.SUCCESS)
                        viewModel.nextWordLevel()
                    },

                    onDismissWinDialog = {
                        viewModel.dismissWinDialog()
                    },

                    onOpenPiggyBank = {
                        viewModel.togglePiggyBank(true)
                    },

                    onOpenStarChest = {
                        viewModel.toggleStarChest(true)
                    },

                    onOpenDailyChallenge = {
                        viewModel.toggleDailyChallenge(true)
                    }
                )

                1 -> CrosswordGameScreen(
                    state = crosswordState,
                    user = user,
                    theme = currentTheme,

                    onSelectCell = { r, c ->
                        triggerFeedback(SoundType.CLICK)
                        viewModel.selectCrosswordCell(r, c)
                    },

                    onInputChar = { char ->
                        triggerFeedback(SoundType.CLICK)
                        viewModel.inputCrosswordChar(char)
                    },

                    onClearCell = {
                        triggerFeedback(SoundType.CLICK)
                        viewModel.clearCrosswordCell()
                    },

                    onUseHint = {
                        triggerFeedback(SoundType.CLICK)
                        viewModel.useCrosswordHint()
                    },

                    onNextLevel = {
                        triggerFeedback(SoundType.SUCCESS)
                        viewModel.nextCrosswordLevel()
                    },

                    onDismissWinDialog = {
                        viewModel.dismissCrosswordWinDialog()
                    }
                )

                2 -> StoreScreen(
                    user = user,
                    tapsellConfig = tapsellConfig,

                    onWatchAd = {
                        viewModel.triggerWatchTapsellAd(activity)
                    },

                    onOpenTapsellGateway = {
                        viewModel.toggleTapsellGateway(true)
                    },

                    onPurchaseVip = { title, price ->
                        viewModel.openPurchaseDialog(
                            title = title,
                            price = price,
                            itemId = "bazaar_vip_sub",
                            isVipPlan = true
                        )
                    },

                    onPurchaseCoins = { title, coins, price ->
                        viewModel.openPurchaseDialog(
                            title = title,
                            price = price,
                            itemId = "bazaar_coins_$coins",
                            isVipPlan = false,
                            coinAmount = coins
                        )
                    }
                )

                3 -> ProfileScreen(
                    user = user,
                    theme = currentTheme,

                    onToggleSound = {
                        viewModel.toggleSound()
                    },

                    onToggleHaptics = {
                        viewModel.toggleHaptics()
                    },

                    onOpenDevGuide = {
                        viewModel.toggleDevMonetizationGuide(true)
                    },

                    onOpenThemeSelector = {
                        viewModel.toggleThemeSelector(true)
                    },

                    onOpenLuckyWheel = {
                        viewModel.toggleLuckyWheel(true)
                    },

                    onOpenPiggyBank = {
                        viewModel.togglePiggyBank(true)
                    },

                    onOpenTapsellGateway = {
                        viewModel.toggleTapsellGateway(true)
                    },

                    onOpenAbout = {
                        viewModel.toggleAboutDialog(true)
                    }
                )
            }

            if (showConfetti) {
                ConfettiOverlay()
            }
        }

        // ---------------------------------------------------------
        // SPECIAL MODALS
        // ---------------------------------------------------------

        // 1. Lucky Wheel
        LuckyWheelDialog(
            isOpen = showLuckyWheel,
            theme = currentTheme,
            coins = user?.coins ?: 0,
            isVip = user?.isVip ?: false,

            onSpinWithCoins = {
                viewModel.spinWheelWithCoins()
            },

            onWatchAdForSpin = {
                viewModel.toggleLuckyWheel(false)
                viewModel.triggerWatchRewardedAd(activity)
            },

            onClaimReward = { slice ->
                triggerFeedback(SoundType.SUCCESS)
                viewModel.claimWheelReward(slice)
            },

            onDismiss = {
                viewModel.toggleLuckyWheel(false)
            }
        )

        // 2. Piggy Bank
        PiggyBankDialog(
            isOpen = showPiggyBank,
            theme = currentTheme,
            piggyCoins = user?.piggyBankCoins ?: 0,
            isVip = user?.isVip ?: false,

            onClaimCoins = {
                triggerFeedback(SoundType.SUCCESS)
                viewModel.claimPiggyBankCoins()
            },

            onWatchAdToBreak = {
                viewModel.togglePiggyBank(false)
                viewModel.triggerWatchRewardedAd(activity)
            },

            onDismiss = {
                viewModel.togglePiggyBank(false)
            }
        )

        // 3. Star Chest
        StarChestDialog(
            isOpen = showStarChest,
            theme = currentTheme,
            starProgress = user?.starChestProgress ?: 0,

            onOpenChest = {
                triggerFeedback(SoundType.SUCCESS)
                viewModel.claimStarChest()
            },

            onDismiss = {
                viewModel.toggleStarChest(false)
            }
        )

        // 4. Daily Challenge
        DailyChallengeDialog(
            isOpen = showDailyChallenge,
            theme = currentTheme,
            dailyStreak = user?.dailyStreak ?: 1,
            isCompletedToday =
                user?.lastDailyChallengeDate == "TODAY",

            onCompleteDaily = {
                triggerFeedback(SoundType.SUCCESS)
                viewModel.completeDailyChallenge()
            },

            onDismiss = {
                viewModel.toggleDailyChallenge(false)
            }
        )

        // 5. Theme Selector
        ThemeSelectorDialog(
            isOpen = showThemeSelector,
            currentThemeId =
                user?.selectedThemeId ?: "turquoise",
            isVip = user?.isVip ?: false,

            onSelectTheme = { themeId ->
                triggerFeedback(SoundType.SUCCESS)
                viewModel.selectTheme(themeId)
            },

            onOpenVipPurchase = {
                viewModel.toggleThemeSelector(false)
                currentTab = 2
            },

            onDismiss = {
                viewModel.toggleThemeSelector(false)
            }
        )

        // 6. Bazaar In-App Billing
        BazaarPaymentDialog(
            purchaseState = purchaseState,

            onConfirm = {
                triggerFeedback(SoundType.SUCCESS)
                viewModel.confirmPurchase()
            },

            onCancel = {
                viewModel.closePurchaseDialog()
            }
        )

        // 7. Developer Monetization Guide
        if (showDevGuide) {
            DeveloperGuideDialog(
                onDismiss = {
                    viewModel.toggleDevMonetizationGuide(false)
                }
            )
        }

        // 8. Tapsell Gateway & Connection
        //
        // This dialog no longer opens a fake ad player.
        // "Test Watch Ad" starts the real Tapsell rewarded flow.
        TapsellGatewayDialog(
            isOpen = showTapsellGatewayDialog,
            config = tapsellConfig,
            isPinging = isPingingTapsell,

            onPingServer = {
                viewModel.pingTapsellServer()
            },

            onSaveConfig = { newConfig ->
                viewModel.updateTapsellConfig(newConfig)
            },

            onTestWatchAd = {
                viewModel.triggerWatchTapsellAd(activity)
            },

            onDismiss = {
                viewModel.toggleTapsellGateway(false)
            }
        )

        // 9. About & Developer Identification
        AboutDialog(
            isOpen = showAboutDialog,

            onDismiss = {
                viewModel.toggleAboutDialog(false)
            }
        )
    }
}
