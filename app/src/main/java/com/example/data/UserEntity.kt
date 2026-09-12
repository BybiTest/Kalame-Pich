package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 150,
    val isVip: Boolean = false,
    val vipPlanName: String = "",
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val levelsCompleted: Int = 0,
    val crosswordsCompleted: Int = 0,
    val adsWatchedCount: Int = 0,
    val bonusWordsCount: Int = 0,
    val xp: Int = 45,
    val piggyBankCoins: Int = 20,
    val selectedThemeId: String = "turquoise",
    val lastSpinTimestamp: Long = 0L,
    val dailyStreak: Int = 2,
    val starChestProgress: Int = 6,
    val lastDailyChallengeDate: String = ""
)
