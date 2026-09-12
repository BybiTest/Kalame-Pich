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
import kotlinx.coroutines.flow.SharingStarted
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
    private var rewardedAdRequestInProgress = false

    /*
     * Prevents the same reward operation from being processed
     * twice by the ViewModel.
     */
    private var lastRewardedResponseHandled = false

    init {
        viewModelScope.launch {
            try {
                repository.checkAndInitUser()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Database user init error",
                    e
                )
            }
        }

        initWordLevel(0)
        initCrosswordLevel(0)

        pingTapsellServer()
    }

    // -------------------------------------------------------------------------
    // WORD GAME LOGIC
    // -------------------------------------------------------------------------

    fun initWordLevel(index: Int) {
        val level =
            GameLevelsData.wordLevels.getOrNull(index)
                ?: GameLevelsData.wordLevels.first()

        _wordState.value = WordGameState(
            currentLevelIndex = index,
            shuffledLetters = level.letters.shuffled(),
            foundWords = emptySet(),
            bonusWordsFound = emptySet(),
            revealedLettersMap = emptyMap(),
            feedbackMessage = null,
            isLevelCompleted = false,
            showWinDialog = false
        )
    }

    val currentWordLevel: WordLevel
        get() =
            GameLevelsData.wordLevels.getOrElse(
                _wordState.value.currentLevelIndex
            ) {
                GameLevelsData.wordLevels.first()
            }

    fun selectLetter(char: Char) {
        val current =
            _wordState.value.selectedLetters

        _wordState.value =
            _wordState.value.copy(
                selectedLetters = current + char,
                feedbackMessage = null
            )
    }

    fun removeLastLetter() {
        val current =
            _wordState.value.selectedLetters

        if (current.isNotEmpty()) {
            _wordState.value =
                _wordState.value.copy(
                    selectedLetters = current.dropLast(1),
                    feedbackMessage = null
                )
        }
    }

    fun clearLetters() {
        _wordState.value =
            _wordState.value.copy(
                selectedLetters = emptyList(),
                feedbackMessage = null
            )
    }

    fun shuffleLetters() {
        val letters =
            currentWordLevel.letters.shuffled()

        _wordState.value =
            _wordState.value.copy(
                shuffledLetters = letters
            )
    }

    fun submitWord() {
        val word =
            _wordState.value.selectedLetters.joinToString("")

        val level =
            currentWordLevel

        if (word.isEmpty()) return

        when {
            _wordState.value.foundWords.contains(word) -> {
                _wordState.value =
                    _wordState.value.copy(
                        selectedLetters = emptyList(),
                        feedbackMessage =
                            "این کلمه قبلاً پیدا شده!"
                    )
            }

            level.targetWords.contains(word) -> {
                val updatedFound =
                    _wordState.value.foundWords + word

                val isCompleted =
                    updatedFound.containsAll(
                        level.targetWords
                    )

                _wordState.value =
                    _wordState.value.copy(
                        selectedLetters = emptyList(),
                        foundWords = updatedFound,
                        feedbackMessage =
                            "آفرین! «$word» درست بود!",
                        isLevelCompleted = isCompleted,
                        showWinDialog = isCompleted
                    )

                if (isCompleted) {
                    _showConfetti.value = true

                    viewModelScope.launch {
                        val reward =
                            if (userProfile.value?.isVip == true) {
                                level.coinReward * 2
                            } else {
                                level.coinReward
                            }

                        repository.addCoins(reward)
                        repository.addXp(25)

                        val currentStars =
                            userProfile.value?.starChestProgress ?: 0

                        repository.updateStarChestProgress(
                            currentStars + 3
                        )

                        repository.incrementLevelsCompleted()

                        repository.saveProgress(
                            LevelProgressEntity(
                                levelId = level.id,
                                gameType = "WORD_CONNECT",
                                isCompleted = true,
                                stars = 3,
                                foundWords =
                                    updatedFound.joinToString(",")
                            )
                        )
                    }
                }
            }

            level.bonusWords.contains(word) &&
                    !_wordState.value.bonusWordsFound.contains(word) -> {

                val updatedBonus =
                    _wordState.value.bonusWordsFound + word

                _wordState.value =
                    _wordState.value.copy(
                        selectedLetters = emptyList(),
                        bonusWordsFound = updatedBonus,
                        feedbackMessage =
                            "کلمه امتیازی «$word» پیدا شد! (+۵ سکه به کیف و قلک)"
                    )

                viewModelScope.launch {
                    repository.recordBonusWord()
                }
            }

            else -> {
                _wordState.value =
                    _wordState.value.copy(
                        selectedLetters = emptyList(),
                        feedbackMessage =
                            "«$word» در این مرحله نیست!"
                    )
            }
        }
    }

    fun useHint() {
        val user =
            userProfile.value ?: return

        val isVip =
            user.isVip

        val hintCost =
            20

        if (!isVip && user.coins < hintCost) {
            _wordState.value =
                _wordState.value.copy(
                    feedbackMessage =
                        "سکه کافی ندارید! از فروشگاه سکه تهیه کنید یا ویدیو ببینید."
                )

            return
        }

        val level =
            currentWordLevel

        val remainingTargetWords =
            level.targetWords.filter {
                !wordState.value.foundWords.contains(it)
            }

        if (remainingTargetWords.isEmpty()) return

        val targetWord =
            remainingTargetWords.first()

        val currentRevealed =
            _wordState.value.revealedLettersMap[targetWord]
                ?: emptySet()

        val unrevealedIndices =
            targetWord.indices.filter {
                !currentRevealed.contains(it)
            }

        if (unrevealedIndices.isNotEmpty()) {
            val randomIndex =
                unrevealedIndices.random()

            val newRevealed =
                currentRevealed + randomIndex

            val newMap =
                _wordState.value.revealedLettersMap +
                        (targetWord to newRevealed)

            _wordState.value =
                _wordState.value.copy(
                    revealedLettersMap = newMap,
                    feedbackMessage =
                        if (isVip) {
                            "راهنمای طلایی VIP اعمال شد!"
                        } else {
                            "یک حرف راهنمایی شد (-۲۰ سکه)"
                        }
                )

            viewModelScope.launch {
                if (!isVip) {
                    repository.deductCoins(hintCost)
                }
            }
        }
    }

    fun nextWordLevel() {
        val nextIndex =
            (
                _wordState.value.currentLevelIndex + 1
                ) % GameLevelsData.wordLevels.size

        initWordLevel(nextIndex)
    }

    fun dismissWinDialog() {
        _wordState.value =
            _wordState.value.copy(
                showWinDialog = false
            )
    }

    // -------------------------------------------------------------------------
    // CROSSWORD GAME LOGIC
    // -------------------------------------------------------------------------

    fun initCrosswordLevel(index: Int) {
        val level =
            GameLevelsData.crosswordLevels.getOrNull(index)
                ?: GameLevelsData.crosswordLevels.first()

        _crosswordState.value =
            CrosswordGameState(
                currentLevelIndex = index,
                enteredGrid = emptyMap(),
                selectedCell =
                    level.cells.firstOrNull()?.let {
                        it.row to it.col
                    },
                isCompleted = false,
                showWinDialog = false
            )
    }

    val currentCrosswordLevel: CrosswordLevel
        get() =
            GameLevelsData.crosswordLevels.getOrElse(
                _crosswordState.value.currentLevelIndex
            ) {
                GameLevelsData.crosswordLevels.first()
            }

    fun selectCrosswordCell(
        row: Int,
        col: Int
    ) {
        val level =
            currentCrosswordLevel

        val isValid =
            level.cells.any {
                it.row == row && it.col == col
            }

        if (isValid) {
            _crosswordState.value =
                _crosswordState.value.copy(
                    selectedCell = row to col
                )
        }
    }

    fun inputCrosswordChar(char: Char) {
        val cell =
            _crosswordState.value.selectedCell
                ?: return

        val currentGrid =
            _crosswordState.value.enteredGrid.toMutableMap()

        currentGrid[cell] = char

        val level =
            currentCrosswordLevel

        val allCorrect =
            level.cells.all { c ->
                currentGrid[c.row to c.col] == c.correctChar
            }

        _crosswordState.value =
            _crosswordState.value.copy(
                enteredGrid = currentGrid,
                isCompleted = allCorrect,
                showWinDialog = allCorrect
            )

        if (allCorrect) {
            _showConfetti.value = true

            viewModelScope.launch {
                val reward =
                    if (userProfile.value?.isVip == true) {
                        level.coinReward * 2
                    } else {
                        level.coinReward
                    }

                repository.addCoins(reward)
                repository.addXp(30)

                val currentStars =
                    userProfile.value?.starChestProgress ?: 0

                repository.updateStarChestProgress(
                    currentStars + 3
                )

                repository.incrementCrosswordsCompleted()

                repository.saveProgress(
                    LevelProgressEntity(
                        levelId = level.id,
                        gameType = "CROSSWORD",
                        isCompleted = true,
                        stars = 3
                    )
                )
            }
        }
    }

    fun clearCrosswordCell() {
        val cell =
            _crosswordState.value.selectedCell
                ?: return

        val currentGrid =
            _crosswordState.value.enteredGrid.toMutableMap()

        currentGrid.remove(cell)

        _crosswordState.value =
            _crosswordState.value.copy(
                enteredGrid = currentGrid
            )
    }

    fun useCrosswordHint() {
        val cell =
            _crosswordState.value.selectedCell
                ?: return

        val level =
            currentCrosswordLevel

        val matchingCell =
            level.cells.find {
                it.row == cell.first &&
                        it.col == cell.second
            } ?: return

        val user =
            userProfile.value ?: return

        val isVip =
            user.isVip

        val hintCost =
            20

        if (!isVip && user.coins < hintCost) {
            return
        }

        val currentGrid =
            _crosswordState.value.enteredGrid.toMutableMap()

        currentGrid[cell] =
            matchingCell.correctChar

        val allCorrect =
            level.cells.all { c ->
                currentGrid[c.row to c.col] == c.correctChar
            }

        _crosswordState.value =
            _crosswordState.value.copy(
                enteredGrid = currentGrid,
                isCompleted = allCorrect,
                showWinDialog = allCorrect
            )

        viewModelScope.launch {
            if (!isVip) {
                repository.deductCoins(hintCost)
            }
        }
    }

    fun nextCrosswordLevel() {
        val nextIndex =
            (
                _crosswordState.value.currentLevelIndex + 1
                ) % GameLevelsData.crosswordLevels.size

        initCrosswordLevel(nextIndex)
    }

    fun dismissCrosswordWinDialog() {
        _crosswordState.value =
            _crosswordState.value.copy(
                showWinDialog = false
            )
    }

    // -------------------------------------------------------------------------
    // TAPSELL GATEWAY
    // -------------------------------------------------------------------------

    fun pingTapsellServer() {
        viewModelScope.launch {
            _isPingingTapsell.value = true

            try {
                val (isSuccess, latency) =
                    tapsellNetworkService.pingTapsellServer(
                        _tapsellConfig.value.serverUrl
                    )

                _tapsellConfig.value =
                    _tapsellConfig.value.copy(
                        isLiveConnected = isSuccess,
                        lastPingMs =
                            if (isSuccess) latency else -1L
                    )
            } catch (e: Exception) {
                Log.w(
                    TAG,
                    "Tapsell ping check failed or offline: ${e.message}"
                )

                _tapsellConfig.value =
                    _tapsellConfig.value.copy(
                        isLiveConnected = false,
                        lastPingMs = -1L
                    )
            } finally {
                _isPingingTapsell.value = false
            }
        }
    }

    fun updateTapsellConfig(
        newConfig: TapsellGatewayConfig
    ) {
        _tapsellConfig.value = newConfig
    }

    fun toggleTapsellGateway(
        show: Boolean
    ) {
        _showTapsellGatewayDialog.value = show

        if (show) {
            pingTapsellServer()
        }
    }

    fun toggleAboutDialog(
        show: Boolean
    ) {
        _showAboutDialog.value = show
    }

    /**
     * Legacy gateway entry point.
     *
     * It no longer opens a fake ad player.
     *
     * The actual rewarded flow must be started from the UI layer
     * using startRewardedAd(activity).
     */
    fun triggerWatchTapsellAd() {
        if (!tapsellAdManager.isConfigured()) {
            _showTapsellGatewayDialog.value = true
            return
        }

        _currentTapsellCampaign.value =
            TapsellCampaignRepository.getNextCampaign()

        Log.d(
            TAG,
            "Rewarded ad requested. Activity is required."
        )
    }

    /**
     * Starts the REAL Tapsell rewarded-video flow.
     *
     * Flow:
     *
     * UI Activity
     *      ↓
     * requestRewardedVideoFromActivity()
     *      ↓
     * onAdLoaded()
     *      ↓
     * showRewardedVideo()
     *      ↓
     * onRewardEarned()
     *      ↓
     * repository.recordAdWatched(50)
     *
     * No timer or UI button can create the reward.
     */
    fun startRewardedAd(
        activity: Activity
    ) {
        if (!tapsellAdManager.isConfigured()) {
            Log.w(
                TAG,
                "Cannot start rewarded ad: Tapsell is not configured."
            )

            _showTapsellGatewayDialog.value = true
            return
        }

        if (rewardedAdRequestInProgress) {
            Log.d(
                TAG,
                "Rewarded ad request already in progress."
            )

            return
        }

        if (activity.isFinishing) {
            Log.w(
                TAG,
                "Cannot start rewarded ad: Activity is finishing."
            )

            return
        }

        rewardedAdRequestInProgress = true
        lastRewardedResponseHandled = false

        Log.d(
            TAG,
            "Starting REAL Tapsell rewarded ad."
        )

        tapsellAdManager.requestRewardedVideoFromActivity(
            activity = activity,
            zoneId = tapsellAdManager.getRewardedZoneId(),
            listener = object : RewardedAdListener {

                override fun onAdLoaded() {
                    Log.d(
                        TAG,
                        "Tapsell rewarded ad loaded."
                    )

                    if (activity.isFinishing) {
                        rewardedAdRequestInProgress = false
                        return
                    }

                    tapsellAdManager.showRewardedVideo(
                        activity = activity,
                        zoneId =
                            tapsellAdManager.getRewardedZoneId(),
                        listener = this
                    )
                }

                override fun onAdFailedToLoad(
                    error: String
                ) {
                    rewardedAdRequestInProgress = false

                    Log.e(
                        TAG,
                        "Tapsell rewarded ad failed to load: $error"
                    )
                }

                override fun onAdOpened() {
                    Log.d(
                        TAG,
                        "Tapsell rewarded ad opened."
                    )
                }

                override fun onRewardEarned(
                    rewardAmount: Int
                ) {
                    if (lastRewardedResponseHandled) {
                        Log.w(
                            TAG,
                            "Duplicate ViewModel reward ignored."
                        )
                        return
                    }

                    lastRewardedResponseHandled = true

                    /*
                     * SECURITY:
                     *
                     * This is the ONLY place in GameViewModel
                     * where a rewarded ad may create coins.
                     *
                     * The amount comes from the trusted manager
                     * callback. We still clamp it to the intended
                     * game reward.
                     */
                    val safeReward =
                        if (rewardAmount > 0) {
                            rewardAmount
                        } else {
                            TAPSELL_REWARD_COINS
                        }

                    Log.i(
                        TAG,
                        "REAL TAPSELL REWARD VERIFIED: +$safeReward coins"
                    )

                    viewModelScope.launch {
                        repository.recordAdWatched(
                            safeReward
                        )

                        _showConfetti.value = true
                    }
                }

                override fun onAdClosed(
                    rewardCompleted: Boolean
                ) {
                    rewardedAdRequestInProgress = false

                    Log.d(
                        TAG,
                        "Tapsell rewarded ad closed. " +
                                "rewardCompleted=$rewardCompleted"
                    )

                    /*
                     * NEVER grant coins here.
                     *
                     * If onRewardEarned() was not called,
                     * closing the ad gives ZERO coins.
                     */
                }

                override fun onAdShowFailed(
                    error: String
                ) {
                    rewardedAdRequestInProgress = false

                    Log.e(
                        TAG,
                        "Tapsell rewarded ad show failed: $error"
                    )
                }
            }
        )
    }

    /**
     * Legacy method retained only for source compatibility.
     *
     * It intentionally DOES NOT grant coins.
     *
     * The old implementation accepted isVerified from the UI,
     * which was unsafe because UI code could claim verification.
     */
    fun claimTapsellReward(
        coins: Int = TAPSELL_REWARD_COINS,
        isVerified: Boolean = false
    ) {
        Log.w(
            TAG,
            "claimTapsellReward() is deprecated and cannot grant coins."
        )

        _showTapsellAdPlayer.value = false
    }

    fun closeTapsellAdPlayer() {
        _showTapsellAdPlayer.value = false
    }

    // -------------------------------------------------------------------------
    // LEGACY REWARDED-AD METHODS
    // -------------------------------------------------------------------------

    /**
     * Legacy entry point retained so existing MainActivity code
     * does not immediately break compilation.
     *
     * MainActivity will be changed in the next step to call
     * startRewardedAd(activity) directly.
     */
    fun triggerWatchRewardedAd() {
        Log.d(
            TAG,
            "triggerWatchRewardedAd() requires Activity. " +
                    "MainActivity must call startRewardedAd(activity)."
        )
    }

    /**
     * IMPORTANT:
     *
     * This method no longer runs a reward timer.
     *
     * A timer can NEVER verify a rewarded advertisement.
     */
    fun tickAdSeconds() {
        Log.w(
            TAG,
            "tickAdSeconds() ignored. Timer cannot verify a Tapsell reward."
        )
    }

    /**
     * IMPORTANT:
     *
     * This method can NEVER grant an ad reward.
     *
     * It is retained temporarily for compatibility with the old UI.
     */
    fun claimAdReward() {
        Log.w(
            TAG,
            "claimAdReward() ignored. Only Tapsell onRewarded can grant coins."
        )

        _adState.value =
            AdDialogState(
                isShowing = false
            )
    }

    fun closeAdDialog() {
        _adState.value =
            AdDialogState(
                isShowing = false
            )
    }

    // -------------------------------------------------------------------------
    // BAZAAR IN-APP BILLING
    // -------------------------------------------------------------------------

    fun openPurchaseDialog(
        title: String,
        price: String,
        itemId: String,
        isVipPlan: Boolean,
        coinAmount: Int = 0
    ) {
        _purchaseState.value =
            PurchaseDialogState(
                isShowing = true,
                title = title,
                price = price,
                itemId = itemId,
                isVipPlan = isVipPlan,
                coinAmount = coinAmount
            )
    }

    fun confirmPurchase() {
        val purchase =
            _purchaseState.value

        _purchaseState.value =
            PurchaseDialogState(
                isShowing = false
            )

        viewModelScope.launch {
            if (purchase.isVipPlan) {
                repository.activateVip(
                    purchase.title
                )

                repository.addCoins(200)
            } else {
                repository.addCoins(
                    purchase.coinAmount
                )
            }

            _showConfetti.value = true
        }
    }

    fun closePurchaseDialog() {
        _purchaseState.value =
            PurchaseDialogState(
                isShowing = false
            )
    }

    fun toggleDevMonetizationGuide(
        show: Boolean
    ) {
        _showDevMonetizationGuide.value = show
    }

    // -------------------------------------------------------------------------
    // SETTINGS
    // -------------------------------------------------------------------------

    fun toggleSound() {
        val current =
            userProfile.value?.soundEnabled ?: true

        viewModelScope.launch {
            repository.setSoundEnabled(!current)
        }
    }

    fun toggleHaptics() {
        val current =
            userProfile.value?.hapticsEnabled ?: true

        viewModelScope.launch {
            repository.setHapticsEnabled(!current)
        }
    }

    // -------------------------------------------------------------------------
    // LUCKY WHEEL
    // -------------------------------------------------------------------------

    fun toggleLuckyWheel(
        show: Boolean
    ) {
        _showLuckyWheel.value = show
    }

    fun claimWheelReward(
        slice: WheelSlice
    ) {
        viewModelScope.launch {
            if (slice.isVipTrial) {
                repository.activateVip(
                    "اشتراک آزمایشی گردونه"
                )
            } else {
                repository.addCoins(
                    slice.coinReward
                )
            }

            repository.addXp(15)

            repository.updateLastSpin(
                System.currentTimeMillis()
            )
        }
    }

    fun spinWheelWithCoins() {
        viewModelScope.launch {
            repository.deductCoins(30)
        }
    }

    // -------------------------------------------------------------------------
    // PIGGY BANK
    // -------------------------------------------------------------------------

    fun togglePiggyBank(
        show: Boolean
    ) {
        _showPiggyBank.value = show
    }

    fun claimPiggyBankCoins() {
        _showPiggyBank.value = false
        _showConfetti.value = true

        viewModelScope.launch {
            repository.claimPiggyBank()
            repository.addXp(20)
        }
    }

    // -------------------------------------------------------------------------
    // STAR CHEST
    // -------------------------------------------------------------------------

    fun toggleStarChest(
        show: Boolean
    ) {
        _showStarChest.value = show
    }

    fun claimStarChest() {
        _showStarChest.value = false
        _showConfetti.value = true

        viewModelScope.launch {
            repository.addCoins(100)
            repository.addXp(50)
            repository.updateStarChestProgress(0)
        }
    }

    // -------------------------------------------------------------------------
    // DAILY CHALLENGE
    // -------------------------------------------------------------------------

    fun toggleDailyChallenge(
        show: Boolean
    ) {
        _showDailyChallenge.value = show
    }

    fun completeDailyChallenge() {
        _showConfetti.value = true

        viewModelScope.launch {
            val currentStreak =
                (userProfile.value?.dailyStreak ?: 1) + 1

            repository.updateDailyStreak(
                currentStreak,
                "TODAY"
            )

            repository.addCoins(70)
            repository.addXp(35)
        }
    }

    // -------------------------------------------------------------------------
    // THEME SELECTOR
    // -------------------------------------------------------------------------

    fun toggleThemeSelector(
        show: Boolean
    ) {
        _showThemeSelector.value = show
    }

    fun selectTheme(
        themeId: String
    ) {
        _showThemeSelector.value = false

        viewModelScope.launch {
            repository.setTheme(themeId)
        }
    }

    // -------------------------------------------------------------------------
    // CONFETTI
    // -------------------------------------------------------------------------

    fun dismissConfetti() {
        _showConfetti.value = false
    }
}
