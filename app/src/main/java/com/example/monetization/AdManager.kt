package com.example.monetization

import android.app.Activity
import android.content.Context

/**
 * Data representation for Native Banner advertisements.
 */
data class NativeAdData(
    val adId: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val callToAction: String,
    val targetUrl: String,
    val sponsoredBy: String = "تپسل (Tapsell)",
    val brandColorHex: Long = 0xFF0081CB
)

/**
 * Callback for Rewarded Video playback lifecycle and reward delivery.
 * Coins/Rewards are strictly verified and only delivered upon [onRewardEarned].
 */
interface RewardedAdListener {
    /** Called when the ad is loaded and ready to be shown. */
    fun onAdLoaded()

    /** Called when the ad fails to load from the ad server. */
    fun onAdFailedToLoad(error: String)

    /** Called when the ad starts displaying on screen. */
    fun onAdOpened()

    /**
     * CRITICAL: Called ONLY when the user has fully watched the ad
     * and the ad provider has verified the reward grant.
     */
    fun onRewardEarned(rewardAmount: Int)

    /** Called when the ad is closed by the user. */
    fun onAdClosed(rewardCompleted: Boolean)

    /** Called when the ad fails to display. */
    fun onAdShowFailed(error: String)
}

/**
 * Callback for Native Banner ad loading.
 */
interface NativeAdListener {
    fun onAdLoaded(adData: NativeAdData)
    fun onAdFailed(error: String)
}

/**
 * Standard AdManager interface for mobile ad networks (Tapsell, AdMob, etc.).
 */
interface AdManager {
    /** Initializes the ad network SDK with the provided credentials. */
    fun initialize(context: Context, appKey: String)

    /** Check if the SDK is initialized with valid credentials. */
    fun isInitialized(): Boolean

    /** Checks if a rewarded video is ready to show. */
    fun isRewardedAdReady(): Boolean

    /** Requests a rewarded video ad from the network. */
    fun requestRewardedVideo(zoneId: String, listener: RewardedAdListener? = null)

    /**
     * Displays a rewarded video ad.
     * The reward is only provided if [RewardedAdListener.onRewardEarned] triggers.
     */
    fun showRewardedVideo(activity: Activity, zoneId: String, listener: RewardedAdListener)

    /** Loads a native banner ad for display. */
    fun requestNativeBanner(zoneId: String, listener: NativeAdListener)
}
