package com.example.ui

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CrosswordLevel
import com.example.data.GameLevelsData
import com.example.data.GameRepository
import com.example.data.LevelProgressEntity
import com.example.data.UserEntity
import com.example.data.WordLevel
import com.example.data.tapsell.TapsellAdCampaign
import com.example.data.tapsell.TapsellCampaignRepository
import com.example.data.tapsell.TapsellGatewayConfig
import com.example.data.tapsell.TapsellNetworkService
import com.example.monetization.BazaarBillingManager
import com.example.monetization.RewardedAdListener
import com.example.monetization.TapsellAdManager
import com.example.ui.components.WheelSlice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WordGameState(
    val currentLevelIndex: Int = 0,
    val selectedLetters: List<Char> = emptyList(),
    val shuffledLetters: List<Char> = emptyList(),
    val foundWords: Set<String> = emptySet(),
    val bonusWordsFound: Set<String> = emptySet(),
    val revealedLettersMap: Map<String, Set<Int>> = emptyMap(),
    val feedbackMessage: String? = null,
    val isLevelCompleted: Boolean = false,
    val showWinDialog: Boolean = false
)

data class CrosswordGameState(
    val currentLevelIndex: Int = 0,
    val enteredGrid: Map<Pair<Int, Int>, Char> = emptyMap(),
    val selectedCell: Pair<Int, Int>? = null,
    val isCompleted: Boolean = false,
    val showWinDialog: Boolean = false
)

/**
 * Legacy dialog state kept temporarily so existing UI code
 * continues to compile while the old fake rewarded dialog
 * is removed from MainActivity.
 *
 * IMPORTANT:
 * This state can NEVER verify or grant a reward.
 */
data class AdDialogState(
    val isShowing: Boolean = false,
    val remainingSeconds: Int = 0,
    val isRewardClaimed: Boolean = false,
    val isRewardVerified: Boolean = false,
    val isConfigured: Boolean = true,
    val statusMessage: String = ""
)

data class PurchaseDialogState(
    val isShowing: Boolean = false,
    val title: String = "",
    val price: String = "",
    val itemId: String = "",
    val isVipPlan: Boolean = false,
    val coinAmount: Int = 0
)

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    companion object {
        private const val TAG = "GameViewModel"

        /**
         * Fixed game reward for one successfully rewarded Tapsell ad.
         *
         * IMPORTANT:
         * The UI cannot change this value.
         */
        private const val TAPSELL_REWARD_COINS = 50
    }

    val userProfile: StateFlow<UserEntity?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserEntity()
        )

    private val _wordState = MutableStateFlow(WordGameState())
    val wordState: StateFlow<WordGameState> = _wordState.asStateFlow()

    private val _crosswordState = MutableStateFlow(CrosswordGameState())
    val crosswordState: StateFlow<CrosswordGameState> =
        _crosswordState.asStateFlow()

    /*
     * Legacy fake-ad state.
     *
     * It is intentionally never used as proof of reward.
     */
    private val _adState = MutableStateFlow(AdDialogState())
    val adState: StateFlow<AdDialogState> = _adState.asStateFlow()

    private val _purchaseState = MutableStateFlow(PurchaseDialogState())
    val purchaseState: StateFlow<PurchaseDialogState> =
        _purchaseState.asStateFlow()

    private val _showDevMonetizationGuide = MutableStateFlow(false)
    val showDevMonetizationGuide: StateFlow<Boolean> =
        _showDevMonetizationGuide.asStateFlow()

    private val _showLuckyWheel = MutableStateFlow(false)
    val showLuckyWheel: StateFlow<Boolean> =
        _showLuckyWheel.asStateFlow()

    private val _showPiggyBank = MutableStateFlow(false)
    val showPiggyBank: StateFlow<Boolean> =
        _showPiggyBank.asStateFlow()

    private val _showStarChest = MutableStateFlow(false)
    val showStarChest: StateFlow<Boolean> =
        _showStarChest.asStateFlow()

    private val _showDailyChallenge = MutableStateFlow(false)
    val showDailyChallenge: StateFlow<Boolean> =
        _showDailyChallenge.asStateFlow()

    private val _showThemeSelector = MutableStateFlow(false)
    val showThemeSelector: StateFlow<Boolean> =
        _showThemeSelector.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> =
        _showConfetti.asStateFlow()

    // -------------------------------------------------------------------------
    // TAPSELL GATEWAY STATE
    // -------------------------------------------------------------------------

    private val _tapsellConfig =
        MutableStateFlow(TapsellGatewayConfig())

    val tapsellConfig: StateFlow<TapsellGatewayConfig> =
        _tapsellConfig.asStateFlow()

    private val _currentTapsellCampaign =
        MutableStateFlow(
            TapsellCampaignRepository.getNextCampaign()
        )

    val currentTapsellCampaign: StateFlow<TapsellAdCampaign> =
        _currentTapsellCampaign.asStateFlow()

    private val _showTapsellAdPlayer =
        MutableStateFlow(false)

    val showTapsellAdPlayer: StateFlow<Boolean> =
        _showTapsellAdPlayer.asStateFlow()

    private val _showTapsellGatewayDialog =
        MutableStateFlow(false)

    val showTapsellGatewayDialog: StateFlow<Boolean> =
        _showTapsellGatewayDialog.asStateFlow()

    private val _isPingingTapsell =
        MutableStateFlow(false)

    val isPingingTapsell: StateFlow<Boolean> =
        _isPingingTapsell.asStateFlow()

    private val _showAboutDialog =
        MutableStateFlow(false)

    val showAboutDialog: StateFlow<Boolean> =
        _showAboutDialog.asStateFlow()

    val tapsellAdManager =
        TapsellAdManager.getInstance()

    val billingManager =
        BazaarBillingManager.getInstance()

    private val tapsellNetworkService =
        TapsellNetworkService()

    /*
     * Prevents two rewarded-ad requests from being started
     * at the same time.
     */
    private var rewardedAdRequestIn
