package com.example.monetization

import android.app.Activity
import android.content.Context

/**
 * Data model for a native/banner-style ad.
 *
 * This model is kept for compatibility with the existing
 * monetization architecture.
 *
 * IMPORTANT:
 * No reward or coins must ever be granted from this model.
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
 * Listener for rewarded-video lifecycle events.
 *
 * SECURITY RULE:
 * Coins must ONLY be granted from onRewardEarned().
 *
 * These callbacks must NEVER be treated as proof of reward:
 * - onAdLoaded()
 * - onAdOpened()
 * - onAdClosed()
 * - onAdFailedToLoad()
 * - onAdShowFailed()
 */
interface RewardedAdListener {

    /**
     * A rewarded ad has been successfully loaded
     * and is ready to be shown.
     */
    fun onAdLoaded()

    /**
     * The rewarded ad failed to load.
     */
    fun onAdFailedToLoad(
        error: String
    )

    /**
     * The rewarded ad was opened/displayed.
     */
    fun onAdOpened()

    /**
     * The Tapsell SDK has explicitly confirmed
     * that the user earned the reward.
     *
     * THIS IS THE ONLY CALLBACK THAT MAY TRIGGER
     * ADDING COINS TO THE USER'S ACCOUNT.
     */
    fun onRewardEarned(
        rewardAmount: Int
    )

    /**
     * The rewarded ad was closed.
     *
     * rewardCompleted indicates whether the SDK's
     * reward callback was already received.
     *
     * This callback MUST NOT independently grant coins.
     */
    fun onAdClosed(
        rewardCompleted: Boolean
    )

    /**
     * The rewarded ad failed while being shown.
     */
    fun onAdShowFailed(
        error: String
    )
}

/**
 * Listener for native/banner-style ad loading.
 *
 * This listener has no reward-related functionality.
 */
interface NativeAdListener {

    /**
     * A native/banner ad was loaded.
     */
    fun onAdLoaded(
        adData: NativeAdData
    )

    /**
     * The ad failed to load.
     */
    fun onAdFailed(
        error: String
    )
}

/**
 * Common abstraction for the application's ad manager.
 *
 * The real implementation is TapsellAdManager.
 *
 * IMPORTANT ARCHITECTURE:
 *
 * ViewModel must not own an Activity.
 * Therefore Activity-dependent operations are exposed
 * explicitly and are intended to be called from the UI layer
 * (for example MainActivity).
 */
interface AdManager {

    /**
     * Initializes the advertising SDK.
     *
     * This should normally be called once from the application/
     * activity startup flow.
     */
    fun initialize(
        context: Context,
        appKey: String
    )

    /**
     * Returns true when the ad SDK has completed
     * successful initialization.
     */
    fun isInitialized(): Boolean

    /**
     * Returns true when a rewarded ad response is ready
     * to be shown.
     */
    fun isRewardedAdReady(): Boolean

    /**
     * Requests a rewarded video.
     *
     * NOTE:
     * The concrete Tapsell implementation requires an Activity
     * for this operation. TapsellAdManager therefore exposes
     * requestRewardedVideoFromActivity().
     *
     * This interface method remains for compatibility with
     * existing project code and must not be used as the actual
     * Tapsell request path.
     */
    fun requestRewardedVideo(
        zoneId: String,
        listener: RewardedAdListener? = null
    )

    /**
     * Shows a previously loaded rewarded video.
     *
     * The actual reward is delivered only through
     * RewardedAdListener.onRewardEarned().
     */
    fun showRewardedVideo(
        activity: Activity,
        zoneId: String,
        listener: RewardedAdListener
    )

    /**
     * Requests a native/banner ad.
     *
     * This is kept as part of the monetization abstraction.
     * The current TapsellAdManager does not yet implement
     * the real banner UI through this method.
     */
    fun requestNativeBanner(
        zoneId: String,
        listener: NativeAdListener
    )
}
