package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val userProfile: Flow<UserEntity?> = gameDao.getUserProfile()

    fun getLevelProgress(levelId: Int, gameType: String): Flow<LevelProgressEntity?> {
        return gameDao.getLevelProgress(levelId, gameType)
    }

    fun getAllProgress(gameType: String): Flow<List<LevelProgressEntity>> {
        return gameDao.getAllProgress(gameType)
    }

    suspend fun checkAndInitUser() {
        if (gameDao.getUserProfileDirect() == null) {
            gameDao.insertUser(UserEntity())
        }
    }

    suspend fun addCoins(amount: Int) = gameDao.addCoins(amount)

    suspend fun deductCoins(amount: Int) = gameDao.deductCoins(amount)

    suspend fun activateVip(planName: String) = gameDao.activateVip(planName)

    suspend fun setSoundEnabled(enabled: Boolean) = gameDao.setSoundEnabled(enabled)

    suspend fun setHapticsEnabled(enabled: Boolean) = gameDao.setHapticsEnabled(enabled)

    suspend fun recordAdWatched(rewardCoins: Int) = gameDao.recordAdWatched(rewardCoins)

    suspend fun incrementLevelsCompleted() = gameDao.incrementLevelsCompleted()

    suspend fun incrementCrosswordsCompleted() = gameDao.incrementCrosswordsCompleted()

    suspend fun recordBonusWord() = gameDao.recordBonusWord()

    suspend fun addXp(amount: Int) = gameDao.addXp(amount)

    suspend fun setTheme(themeId: String) = gameDao.setTheme(themeId)

    suspend fun claimPiggyBank() = gameDao.claimPiggyBank()

    suspend fun updateLastSpin(timestamp: Long) = gameDao.updateLastSpin(timestamp)

    suspend fun updateStarChestProgress(progress: Int) = gameDao.updateStarChestProgress(progress)

    suspend fun updateDailyStreak(streak: Int, date: String) = gameDao.updateDailyStreak(streak, date)

    suspend fun saveProgress(progress: LevelProgressEntity) = gameDao.saveProgress(progress)
}
