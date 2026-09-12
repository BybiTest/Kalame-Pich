package com.example.monetization

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.BuildConfig
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel

/**
 * Real Tapsell Plus implementation.
 *
 * Important security rule:
 * Coins/rewards must ONLY be granted from the real
 * TapsellPlus AdShowListener.onRewarded callback.
 *
 * Closing the ad, waiting for a timer, clicking a button,
 * or dismissing a dialog NEVER grants a reward.
 */
class TapsellAdManager private constructor() : AdManager {

    companion object {
        private const val TAG = "TapsellAdManager"

        @Volatile
        private var instance: TapsellAdManager? = null

        fun getInstance(): TapsellAdManager {
            return instance ?: synchronized(this) {
                instance ?: TapsellAdManager().also {
                    instance = it
                }
            }
        }
    }

    private var appKey: String = BuildConfig.TAPSELL_APP_KEY
    private var rewardedZoneId: String = BuildConfig.TAPSELL_REWARDED_ZONE_ID
    private var bannerZoneId: String = BuildConfig.TAPSELL_BANNER_ZONE_ID

    private var initialized = false

    /**
     * Response ID returned by Tapsell after a successful
     * rewarded-video request.
     */
    private var rewardedResponseId: String? = null

    /**
     * Prevents duplicate reward delivery for the same ad.
     */
    private var rewardDeliveredForResponseId: String? = null

    override fun initialize(context: Context, appKey: String) {
        this.appKey = if (appKey.isNotBlank()) {
            appKey
        } else {
            BuildConfig.TAPSELL_APP_KEY
        }

        rewardedZoneId = BuildConfig.TAPSELL_REWARDED_ZONE_ID
        bannerZoneId = BuildConfig.TAPSELL_BANNER_ZONE_ID

        if (!isConfigured()) {
            initialized = false

            Log.w(
                TAG,
                "Tapsell is not configured. " +
                        "Set TAPSELL_APP_KEY and Tapsell Zone IDs."
            )

            return
        }

        try {
            TapsellPlus.initialize(
                context.applicationContext,
                this.appKey
            )

            initialized = true

            Log.i(
                TAG,
                "Tapsell Plus initialized successfully."
            )
        } catch (exception: Exception) {
            initialized = false

            Log.e(
                TAG,
                "Tapsell Plus initialization failed.",
                exception
            )
        }
    }

    /**
     * Returns true only when a real App Key exists.
     */
    fun isConfigured(): Boolean {
        return appKey.isNotBlank() &&
                !appKey.contains(
                    "YOUR_TAPSELL_APP_KEY",
                    ignoreCase = true
                )
    }

    override fun isInitialized(): Boolean {
        return initialized
    }

    /**
     * A rewarded ad is ready only when Tapsell has returned
     * a real response ID.
     */
    override fun isRewardedAdReady(): Boolean {
        return isConfigured() &&
                initialized &&
                !rewardedResponseId.isNullOrBlank()
    }

    fun updateZones(
        rewardedZone: String,
        bannerZone: String
    ) {
        if (rewardedZone.isNotBlank()) {
            rewardedZoneId = rewardedZone
        }

        if (bannerZone.isNotBlank()) {
            bannerZoneId = bannerZone
        }
    }

    fun getAppKey(): String = appKey

    fun getRewardedZoneId(): String = rewardedZoneId

    fun getBannerZoneId(): String = bannerZoneId

    /**
     * Requests a real rewarded video from Tapsell Plus.
     *
     * IMPORTANT:
     * This function does NOT grant any reward.
     * Reward is granted only inside onRewarded().
     */
    override fun requestRewardedVideo(
        zoneId: String,
        listener: RewardedAdListener?
    ) {
        if (!isConfigured()) {
            val errorMessage =
                "Tapsell App Key is not configured."

            Log.e(TAG, errorMessage)

            listener?.onAdFailedToLoad(errorMessage)

            return
        }

        if (!initialized) {
            val errorMessage =
                "Tapsell SDK is not initialized."

            Log.e(TAG, errorMessage)

            listener?.onAdFailedToLoad(errorMessage)

            return
        }

        val targetZone =
            if (zoneId.isNotBlank()) {
                zoneId
            } else {
                rewardedZoneId
            }

        if (targetZone.isBlank() ||
            targetZone.contains(
                "YOUR_TAPSELL_REWARDED_ZONE_ID",
                ignoreCase = true
            )
        ) {
            val errorMessage =
                "Tapsell rewarded Zone ID is not configured."

            Log.e(TAG, errorMessage)

            listener?.onAdFailedToLoad(errorMessage)

            return
        }

        // Clear the previous response before requesting a new one.
        rewardedResponseId = null
        rewardDeliveredForResponseId = null

        Log.d(
            TAG,
            "Requesting real rewarded video. Zone=$targetZone"
        )

        try {
            TapsellPlus.requestRewardedVideoAd(
                targetZone,
                object : AdRequestCallback() {

                    override fun response(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.response(tapsellPlusAdModel)

                        val responseId =
                            tapsellPlusAdModel.responseId

                        if (responseId.isNullOrBlank()) {
                            val errorMessage =
                                "Tapsell returned an empty response ID."

                            Log.e(TAG, errorMessage)

                            listener?.onAdFailedToLoad(
                                errorMessage
                            )

                            return
                        }

                        rewardedResponseId = responseId

                        Log.d(
                            TAG,
                            "Rewarded video loaded successfully."
                        )

                        listener?.onAdLoaded()
                    }

                    override fun error(
                        message: String
                    ) {
                        super.error(message)

                        rewardedResponseId = null

                        Log.e(
                            TAG,
                            "Rewarded video request failed: $message"
                        )

                        listener?.onAdFailedToLoad(message)
                    }
                }
            )
        } catch (exception: Exception) {
            rewardedResponseId = null

            val errorMessage =
                exception.message
                    ?: "Unknown Tapsell request error."

            Log.e(
                TAG,
                "Exception while requesting rewarded video.",
                exception
            )

            listener?.onAdFailedToLoad(errorMessage)
        }
    }

    /**
     * Shows the real rewarded video.
     *
     * IMPORTANT:
     * No reward is granted from onClosed().
     * The only reward path is onRewarded().
     */
    override fun showRewardedVideo(
        activity: Activity,
        zoneId: String,
        listener: RewardedAdListener
    ) {
        if (!isConfigured()) {
            val errorMessage =
                "Tapsell App Key is not configured."

            Log.e(TAG, errorMessage)

            listener.onAdShowFailed(errorMessage)

            return
        }

        if (!initialized) {
            val errorMessage =
                "Tapsell SDK is not initialized."

            Log.e(TAG, errorMessage)

            listener.onAdShowFailed(errorMessage)

            return
        }

        val responseId = rewardedResponseId

        if (responseId.isNullOrBlank()) {
            val errorMessage =
                "No rewarded video is ready. Request an ad first."

            Log.e(TAG, errorMessage)

            listener.onAdShowFailed(errorMessage)

            return
        }

        // Consume the response ID immediately so the same ad
        // cannot be shown twice accidentally.
        rewardedResponseId = null

        Log.d(
            TAG,
            "Showing real rewarded video."
        )

        try {
            TapsellPlus.showRewardedVideoAd(
                activity,
                responseId,
                object : AdShowListener() {

                    override fun onOpened(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.onOpened(tapsellPlusAdModel)

                        Log.d(
                            TAG,
                            "Rewarded video opened."
                        )

                        listener.onAdOpened()
                    }

                    override fun onClosed(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.onClosed(tapsellPlusAdModel)

                        Log.d(
                            TAG,
                            "Rewarded video closed."
                        )

                        // NEVER grant coins here.
                        listener.onAdClosed(
                            rewardCompleted =
                                rewardDeliveredForResponseId ==
                                        responseId
                        )
                    }

                    override fun onRewarded(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.onRewarded(tapsellPlusAdModel)

                        /*
                         * This is the ONLY place where the app
                         * considers the Tapsell reward verified.
                         */
                        if (
                            rewardDeliveredForResponseId ==
                            responseId
                        ) {
                            Log.w(
                                TAG,
                                "Duplicate reward callback ignored."
                            )

                            return
                        }

                        rewardDeliveredForResponseId =
                            responseId

                        Log.i(
                            TAG,
                            "REAL TAPSELL REWARD VERIFIED."
                        )

                        listener.onRewardEarned(
                            rewardAmount = 50
                        )
                    }

                    override fun onError(
                        tapsellPlusErrorModel:
                        TapsellPlusErrorModel
                    ) {
                        super.onError(
                            tapsellPlusErrorModel
                        )

                        val errorMessage =
                            tapsellPlusErrorModel.toString()

                        Log.e(
                            TAG,
                            "Rewarded video show error: $errorMessage"
                        )

                        listener.onAdShowFailed(
                            errorMessage
                        )
                    }
                }
            )
        } catch (exception: Exception) {
            val errorMessage =
                exception.message
                    ?: "Unknown Tapsell show error."

            Log.e(
                TAG,
                "Exception while showing rewarded video.",
                exception
            )

            listener.onAdShowFailed(
                errorMessage
            )
        }
    }

    /**
     * Native banner is intentionally not simulated anymore.
     *
     * The previous implementation created fake advertisement
     * data, which must never be used in a production monetized app.
     */
    override fun requestNativeBanner(
        zoneId: String,
        listener: NativeAdListener
    ) {
        val errorMessage =
            "Native banner simulation has been disabled. " +
                    "Use the real Tapsell Standard Banner SDK integration."

        Log.w(TAG, errorMessage)

        listener.onAdFailed(errorMessage)
    }

    /**
     * Clears the currently cached rewarded response.
     */
    fun clearRewardedAd() {
        rewardedResponseId = null
        rewardDeliveredForResponseId = null
    }

    private fun maskKey(key: String): String {
        return if (key.length > 8) {
            "${key.take(4)}...${key.takeLast(4)}"
        } else {
            "***"
        }
    }
}
