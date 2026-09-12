package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CrosswordCell
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
    val revealedLettersMap: Map<String, Set<Int>> = emptyMap(), // targetWord -> indices revealed
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

data class AdDialogState(
    val isShowing: Boolean = false,
    val remainingSeconds: Int = 5,
    val isRewardClaimed: Boolean = false
)

data class PurchaseDialogState(
    val isShowing: Boolean = false,
    val title: String = "",
    val price: String = "",
    val itemId: String = "",
    val isVipPlan: Boolean = false,
    val coinAmount: Int = 0
)

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val userProfile: StateFlow<UserEntity?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserEntity()
        )

    private val _wordState = MutableStateFlow(WordGameState())
    val wordState: StateFlow<WordGameState> = _wordState.asStateFlow()

    private val _crosswordState = MutableStateFlow(CrosswordGameState())
    val crosswordState: StateFlow<CrosswordGameState> = _crosswordState.asStateFlow()

    private val _adState = MutableStateFlow(AdDialogState())
    val adState: StateFlow<AdDialogState> = _adState.asStateFlow()

    private val _purchaseState = MutableStateFlow(PurchaseDialogState())
    val purchaseState: StateFlow<PurchaseDialogState> = _purchaseState.asStateFlow()

    private val _showDevMonetizationGuide = MutableStateFlow(false)
    val showDevMonetizationGuide: StateFlow<Boolean> = _showDevMonetizationGuide.asStateFlow()

    private val _showLuckyWheel = MutableStateFlow(false)
    val showLuckyWheel: StateFlow<Boolean> = _showLuckyWheel.asStateFlow()

    private val _showPiggyBank = MutableStateFlow(false)
    val showPiggyBank: StateFlow<Boolean> = _showPiggyBank.asStateFlow()

    private val _showStarChest = MutableStateFlow(false)
    val showStarChest: StateFlow<Boolean> = _showStarChest.asStateFlow()

    private val _showDailyChallenge = MutableStateFlow(false)
    val showDailyChallenge: StateFlow<Boolean> = _showDailyChallenge.asStateFlow()

    private val _showThemeSelector = MutableStateFlow(false)
    val showThemeSelector: StateFlow<Boolean> = _showThemeSelector.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    // --- TAPSELL AD GATEWAY STATE ---
    private val _tapsellConfig = MutableStateFlow(TapsellGatewayConfig())
    val tapsellConfig: StateFlow<TapsellGatewayConfig> = _tapsellConfig.asStateFlow()

    private val _currentTapsellCampaign = MutableStateFlow(TapsellCampaignRepository.getNextCampaign())
    val currentTapsellCampaign: StateFlow<TapsellAdCampaign> = _currentTapsellCampaign.asStateFlow()

    private val _showTapsellAdPlayer = MutableStateFlow(false)
    val showTapsellAdPlayer: StateFlow<Boolean> = _showTapsellAdPlayer.asStateFlow()

    private val _showTapsellGatewayDialog = MutableStateFlow(false)
    val showTapsellGatewayDialog: StateFlow<Boolean> = _showTapsellGatewayDialog.asStateFlow()

    private val _isPingingTapsell = MutableStateFlow(false)
    val isPingingTapsell: StateFlow<Boolean> = _isPingingTapsell.asStateFlow()

    private val tapsellNetworkService = TapsellNetworkService()

    init {
        viewModelScope.launch {
            repository.checkAndInitUser()
        }
        initWordLevel(0)
        initCrosswordLevel(0)
        // Perform initial ping to Tapsell website (tapsell.ir)
        pingTapsellServer()
    }

    // --- WORD GAME LOGIC ---

    fun initWordLevel(index: Int) {
        val level = GameLevelsData.wordLevels.getOrNull(index) ?: GameLevelsData.wordLevels.first()
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
        get() = GameLevelsData.wordLevels.getOrElse(_wordState.value.currentLevelIndex) { GameLevelsData.wordLevels.first() }

    fun selectLetter(char: Char) {
        val current = _wordState.value.selectedLetters
        _wordState.value = _wordState.value.copy(
            selectedLetters = current + char,
            feedbackMessage = null
        )
    }

    fun removeLastLetter() {
        val current = _wordState.value.selectedLetters
        if (current.isNotEmpty()) {
            _wordState.value = _wordState.value.copy(
                selectedLetters = current.dropLast(1),
                feedbackMessage = null
            )
        }
    }

    fun clearLetters() {
        _wordState.value = _wordState.value.copy(
            selectedLetters = emptyList(),
            feedbackMessage = null
        )
    }

    fun shuffleLetters() {
        val letters = currentWordLevel.letters.shuffled()
        _wordState.value = _wordState.value.copy(shuffledLetters = letters)
    }

    fun submitWord() {
        val word = _wordState.value.selectedLetters.joinToString("")
        val level = currentWordLevel

        if (word.isEmpty()) return

        when {
            _wordState.value.foundWords.contains(word) -> {
                _wordState.value = _wordState.value.copy(
                    selectedLetters = emptyList(),
                    feedbackMessage = "این کلمه قبلاً پیدا شده!"
                )
            }
            level.targetWords.contains(word) -> {
                val updatedFound = _wordState.value.foundWords + word
                val isCompleted = updatedFound.containsAll(level.targetWords)

                _wordState.value = _wordState.value.copy(
                    selectedLetters = emptyList(),
                    foundWords = updatedFound,
                    feedbackMessage = "آفرین! «$word» درست بود!",
                    isLevelCompleted = isCompleted,
                    showWinDialog = isCompleted
                )

                if (isCompleted) {
                    _showConfetti.value = true
                    viewModelScope.launch {
                        val reward = if (userProfile.value?.isVip == true) level.coinReward * 2 else level.coinReward
                        repository.addCoins(reward)
                        repository.addXp(25)
                        val currentStars = userProfile.value?.starChestProgress ?: 0
                        repository.updateStarChestProgress(currentStars + 3)
                        repository.incrementLevelsCompleted()
                        repository.saveProgress(
                            LevelProgressEntity(
                                levelId = level.id,
                                gameType = "WORD_CONNECT",
                                isCompleted = true,
                                stars = 3,
                                foundWords = updatedFound.joinToString(",")
                            )
                        )
                    }
                }
            }
            level.bonusWords.contains(word) && !_wordState.value.bonusWordsFound.contains(word) -> {
                val updatedBonus = _wordState.value.bonusWordsFound + word
                _wordState.value = _wordState.value.copy(
                    selectedLetters = emptyList(),
                    bonusWordsFound = updatedBonus,
                    feedbackMessage = "کلمه امتیازی «$word» پیدا شد! (+۵ سکه به کیف و قلک)"
                )
                viewModelScope.launch {
                    repository.recordBonusWord()
                }
            }
            else -> {
                _wordState.value = _wordState.value.copy(
                    selectedLetters = emptyList(),
                    feedbackMessage = "«$word» در این مرحله نیست!"
                )
            }
        }
    }

    fun useHint() {
        val user = userProfile.value ?: return
        val isVip = user.isVip
        val hintCost = 20

        if (!isVip && user.coins < hintCost) {
            _wordState.value = _wordState.value.copy(
                feedbackMessage = "سکه کافی ندارید! از فروشگاه سکه تهیه کنید یا ویدیو ببینید."
            )
            return
        }

        val level = currentWordLevel
        val remainingTargetWords = level.targetWords.filter { !wordState.value.foundWords.contains(it) }

        if (remainingTargetWords.isEmpty()) return

        val targetWord = remainingTargetWords.first()
        val currentRevealed = _wordState.value.revealedLettersMap[targetWord] ?: emptySet()
        val unrevealedIndices = targetWord.indices.filter { !currentRevealed.contains(it) }

        if (unrevealedIndices.isNotEmpty()) {
            val randomIndex = unrevealedIndices.random()
            val newRevealed = currentRevealed + randomIndex
            val newMap = _wordState.value.revealedLettersMap + (targetWord to newRevealed)

            _wordState.value = _wordState.value.copy(
                revealedLettersMap = newMap,
                feedbackMessage = if (isVip) "راهنمای طلایی VIP اعمال شد!" else "یک حرف راهنمایی شد (-۲۰ سکه)"
            )

            viewModelScope.launch {
                if (!isVip) {
                    repository.deductCoins(hintCost)
                }
            }
        }
    }

    fun nextWordLevel() {
        val nextIndex = (_wordState.value.currentLevelIndex + 1) % GameLevelsData.wordLevels.size
        initWordLevel(nextIndex)
    }

    fun dismissWinDialog() {
        _wordState.value = _wordState.value.copy(showWinDialog = false)
    }

    // --- CROSSWORD GAME LOGIC ---

    fun initCrosswordLevel(index: Int) {
        val level = GameLevelsData.crosswordLevels.getOrNull(index) ?: GameLevelsData.crosswordLevels.first()
        _crosswordState.value = CrosswordGameState(
            currentLevelIndex = index,
            enteredGrid = emptyMap(),
            selectedCell = level.cells.firstOrNull()?.let { it.row to it.col },
            isCompleted = false,
            showWinDialog = false
        )
    }

    val currentCrosswordLevel: CrosswordLevel
        get() = GameLevelsData.crosswordLevels.getOrElse(_crosswordState.value.currentLevelIndex) { GameLevelsData.crosswordLevels.first() }

    fun selectCrosswordCell(row: Int, col: Int) {
        val level = currentCrosswordLevel
        val isValid = level.cells.any { it.row == row && it.col == col }
        if (isValid) {
            _crosswordState.value = _crosswordState.value.copy(selectedCell = row to col)
        }
    }

    fun inputCrosswordChar(char: Char) {
        val cell = _crosswordState.value.selectedCell ?: return
        val currentGrid = _crosswordState.value.enteredGrid.toMutableMap()
        currentGrid[cell] = char

        val level = currentCrosswordLevel
        // Check if all cells filled correctly
        val allCorrect = level.cells.all { c ->
            currentGrid[c.row to c.col] == c.correctChar
        }

        _crosswordState.value = _crosswordState.value.copy(
            enteredGrid = currentGrid,
            isCompleted = allCorrect,
            showWinDialog = allCorrect
        )

        if (allCorrect) {
            _showConfetti.value = true
            viewModelScope.launch {
                val reward = if (userProfile.value?.isVip == true) level.coinReward * 2 else level.coinReward
                repository.addCoins(reward)
                repository.addXp(30)
                val currentStars = userProfile.value?.starChestProgress ?: 0
                repository.updateStarChestProgress(currentStars + 3)
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
        val cell = _crosswordState.value.selectedCell ?: return
        val currentGrid = _crosswordState.value.enteredGrid.toMutableMap()
        currentGrid.remove(cell)
        _crosswordState.value = _crosswordState.value.copy(enteredGrid = currentGrid)
    }

    fun useCrosswordHint() {
        val cell = _crosswordState.value.selectedCell ?: return
        val level = currentCrosswordLevel
        val matchingCell = level.cells.find { it.row == cell.first && it.col == cell.second } ?: return

        val user = userProfile.value ?: return
        val isVip = user.isVip
        val hintCost = 20

        if (!isVip && user.coins < hintCost) return

        val currentGrid = _crosswordState.value.enteredGrid.toMutableMap()
        currentGrid[cell] = matchingCell.correctChar

        val allCorrect = level.cells.all { c -> currentGrid[c.row to c.col] == c.correctChar }

        _crosswordState.value = _crosswordState.value.copy(
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
        val nextIndex = (_crosswordState.value.currentLevelIndex + 1) % GameLevelsData.crosswordLevels.size
        initCrosswordLevel(nextIndex)
    }

    fun dismissCrosswordWinDialog() {
        _crosswordState.value = _crosswordState.value.copy(showWinDialog = false)
    }

    // --- TAPSELL AD GATEWAY ACTIONS ---

    fun pingTapsellServer() {
        viewModelScope.launch {
            _isPingingTapsell.value = true
            val (isSuccess, latency) = tapsellNetworkService.pingTapsellServer(_tapsellConfig.value.serverUrl)
            _tapsellConfig.value = _tapsellConfig.value.copy(
                isLiveConnected = isSuccess,
                lastPingMs = if (isSuccess) latency else -1L
            )
            _isPingingTapsell.value = false
        }
    }

    fun updateTapsellConfig(newConfig: TapsellGatewayConfig) {
        _tapsellConfig.value = newConfig
    }

    fun toggleTapsellGateway(show: Boolean) {
        _showTapsellGatewayDialog.value = show
        if (show) {
            pingTapsellServer()
        }
    }

    fun triggerWatchTapsellAd() {
        _currentTapsellCampaign.value = TapsellCampaignRepository.getNextCampaign()
        _showTapsellAdPlayer.value = true
    }

    fun claimTapsellReward(coins: Int = 50) {
        _showTapsellAdPlayer.value = false
        viewModelScope.launch {
            repository.recordAdWatched(coins)
            _showConfetti.value = true
        }
    }

    fun closeTapsellAdPlayer() {
        _showTapsellAdPlayer.value = false
    }

    // --- REWARDED ADS (REDIRECT TO TAPSELL GATEWAY) ---

    fun triggerWatchRewardedAd() {
        triggerWatchTapsellAd()
    }

    fun tickAdSeconds() {
        val current = _adState.value.remainingSeconds
        if (current > 1) {
            _adState.value = _adState.value.copy(remainingSeconds = current - 1)
        } else {
            _adState.value = _adState.value.copy(remainingSeconds = 0, isRewardClaimed = true)
        }
    }

    fun claimAdReward() {
        _adState.value = AdDialogState(isShowing = false)
        viewModelScope.launch {
            repository.recordAdWatched(50)
            _showConfetti.value = true
        }
    }

    fun closeAdDialog() {
        _adState.value = AdDialogState(isShowing = false)
    }

    // --- BAZAAR IN-APP BILLING SIMULATOR ---

    fun openPurchaseDialog(title: String, price: String, itemId: String, isVipPlan: Boolean, coinAmount: Int = 0) {
        _purchaseState.value = PurchaseDialogState(
            isShowing = true,
            title = title,
            price = price,
            itemId = itemId,
            isVipPlan = isVipPlan,
            coinAmount = coinAmount
        )
    }

    fun confirmPurchase() {
        val purchase = _purchaseState.value
        _purchaseState.value = PurchaseDialogState(isShowing = false)

        viewModelScope.launch {
            if (purchase.isVipPlan) {
                repository.activateVip(purchase.title)
                repository.addCoins(200) // Bonus gift on VIP purchase!
            } else {
                repository.addCoins(purchase.coinAmount)
            }
        }
    }

    fun closePurchaseDialog() {
        _purchaseState.value = PurchaseDialogState(isShowing = false)
    }

    fun toggleDevMonetizationGuide(show: Boolean) {
        _showDevMonetizationGuide.value = show
    }

    fun toggleSound() {
        val current = userProfile.value?.soundEnabled ?: true
        viewModelScope.launch {
            repository.setSoundEnabled(!current)
        }
    }

    fun toggleHaptics() {
        val current = userProfile.value?.hapticsEnabled ?: true
        viewModelScope.launch {
            repository.setHapticsEnabled(!current)
        }
    }

    // --- LUCKY WHEEL ---
    fun toggleLuckyWheel(show: Boolean) {
        _showLuckyWheel.value = show
    }

    fun claimWheelReward(slice: WheelSlice) {
        viewModelScope.launch {
            if (slice.isVipTrial) {
                repository.activateVip("اشتراک آزمایشی گردونه")
            } else {
                repository.addCoins(slice.coinReward)
            }
            repository.addXp(15)
            repository.updateLastSpin(System.currentTimeMillis())
        }
    }

    fun spinWheelWithCoins() {
        viewModelScope.launch {
            repository.deductCoins(30)
        }
    }

    // --- PIGGY BANK ---
    fun togglePiggyBank(show: Boolean) {
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

    // --- STAR CHEST ---
    fun toggleStarChest(show: Boolean) {
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

    // --- DAILY CHALLENGE ---
    fun toggleDailyChallenge(show: Boolean) {
        _showDailyChallenge.value = show
    }

    fun completeDailyChallenge() {
        _showConfetti.value = true
        viewModelScope.launch {
            val currentStreak = (userProfile.value?.dailyStreak ?: 1) + 1
            repository.updateDailyStreak(currentStreak, "TODAY")
            repository.addCoins(70)
            repository.addXp(35)
        }
    }

    // --- THEME SELECTOR ---
    fun toggleThemeSelector(show: Boolean) {
        _showThemeSelector.value = show
    }

    fun selectTheme(themeId: String) {
        _showThemeSelector.value = false
        viewModelScope.launch {
            repository.setTheme(themeId)
        }
    }

    fun dismissConfetti() {
        _showConfetti.value = false
    }
}

