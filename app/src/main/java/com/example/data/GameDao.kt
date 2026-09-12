package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileDirect(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE user_profile SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user_profile SET coins = MAX(0, coins - :amount) WHERE id = 1")
    suspend fun deductCoins(amount: Int)

    @Query("UPDATE user_profile SET isVip = 1, vipPlanName = :planName WHERE id = 1")
    suspend fun activateVip(planName: String)

    @Query("UPDATE user_profile SET soundEnabled = :enabled WHERE id = 1")
    suspend fun setSoundEnabled(enabled: Boolean)

    @Query("UPDATE user_profile SET hapticsEnabled = :enabled WHERE id = 1")
    suspend fun setHapticsEnabled(enabled: Boolean)

    @Query("UPDATE user_profile SET adsWatchedCount = adsWatchedCount + 1, coins = coins + :rewardCoins WHERE id = 1")
    suspend fun recordAdWatched(rewardCoins: Int)

    @Query("UPDATE user_profile SET levelsCompleted = levelsCompleted + 1 WHERE id = 1")
    suspend fun incrementLevelsCompleted()

    @Query("UPDATE user_profile SET crosswordsCompleted = crosswordsCompleted + 1 WHERE id = 1")
    suspend fun incrementCrosswordsCompleted()

    @Query("UPDATE user_profile SET bonusWordsCount = bonusWordsCount + 1, coins = coins + 5, piggyBankCoins = piggyBankCoins + 5, xp = xp + 10 WHERE id = 1")
    suspend fun recordBonusWord()

    @Query("UPDATE user_profile SET xp = xp + :amount WHERE id = 1")
    suspend fun addXp(amount: Int)

    @Query("UPDATE user_profile SET selectedThemeId = :themeId WHERE id = 1")
    suspend fun setTheme(themeId: String)

    @Query("UPDATE user_profile SET coins = coins + piggyBankCoins, piggyBankCoins = 0 WHERE id = 1")
    suspend fun claimPiggyBank()

    @Query("UPDATE user_profile SET lastSpinTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastSpin(timestamp: Long)

    @Query("UPDATE user_profile SET starChestProgress = :progress WHERE id = 1")
    suspend fun updateStarChestProgress(progress: Int)

    @Query("UPDATE user_profile SET dailyStreak = :streak, lastDailyChallengeDate = :date WHERE id = 1")
    suspend fun updateDailyStreak(streak: Int, date: String)

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId AND gameType = :gameType")
    fun getLevelProgress(levelId: Int, gameType: String): Flow<LevelProgressEntity?>

    @Query("SELECT * FROM level_progress WHERE gameType = :gameType")
    fun getAllProgress(gameType: String): Flow<List<LevelProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LevelProgressEntity)
}
