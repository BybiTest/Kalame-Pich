package com.example.monetization

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.example.BuildConfig
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel

/**
 * Real Tapsell Plus rewarded-ad manager.
 *
 * SECURITY RULE:
 * The app must grant coins ONLY from the real
 * TapsellPlus AdShowListener.onRewarded() callback.
 *
 * Closing the ad, a timer, a button click, or a fake
 * dialog NEVER grants a reward.
 */
class TapsellAdManager private constructor() : AdManager {

    companion object {

        private const val TAG = "TapsellAdManager"

        private const val REWARD_COINS = 50

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

    private var appKey: String =
        BuildConfig.TAPSELL_APP_KEY

    private var rewardedZoneId: String =
        BuildConfig.TAPSELL_REWARDED_ZONE_ID

    private var bannerZoneId: String =
        BuildConfig.TAPSELL_BANNER_ZONE_ID

    private var initialized = false

    /**
     * Response ID returned by Tapsell after a successful
     * rewarded-video request.
     */
    private var rewardedResponseId: String? = null

    /**
     * Response ID for which the reward callback has already
     * been delivered.
     *
     * This prevents duplicate rewards.
     */
    private var rewardedResponseAlreadyDelivered: String? = null

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------

    override fun initialize(
        context: Context,
        appKey: String
    ) {
        this.appKey =
            if (appKey.isNotBlank()) {
                appKey
            } else {
                BuildConfig.TAPSELL_APP_KEY
            }

        rewardedZoneId =
            BuildConfig.TAPSELL_REWARDED_ZONE_ID

        bannerZoneId =
            BuildConfig.TAPSELL_BANNER_ZONE_ID

        if (!isConfigured()) {
            initialized = false

            Log.w(
                TAG,
                "Tapsell is not configured. " +
                        "TAPSELL_APP_KEY is missing."
            )

            return
        }

        try {
            /*
             * Tapsell Plus SDK initialization.
             */
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

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

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

    fun getAppKey(): String {
        return appKey
    }

    fun getRewardedZoneId(): String {
        return rewardedZoneId
    }

    fun getBannerZoneId(): String {
        return bannerZoneId
    }

    // -------------------------------------------------------------------------
    // Request rewarded video
    // -------------------------------------------------------------------------

    override fun requestRewardedVideo(
        zoneId: String,
        listener: RewardedAdListener?
    ) {
        if (!isConfigured()) {

            val errorMessage =
                "Tapsell App Key is not configured."

            Log.e(
                TAG,
                errorMessage
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )

            return
        }

        if (!initialized) {

            val errorMessage =
                "Tapsell SDK is not initialized."

            Log.e(
                TAG,
                errorMessage
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )

            return
        }

        val targetZone =
            if (zoneId.isNotBlank()) {
                zoneId
            } else {
                rewardedZoneId
            }

        if (
            targetZone.isBlank() ||
            targetZone.contains(
                "YOUR_TAPSELL_REWARDED_ZONE_ID",
                ignoreCase = true
            )
        ) {

            val errorMessage =
                "Tapsell rewarded Zone ID is not configured."

            Log.e(
                TAG,
                errorMessage
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )

            return
        }

        /*
         * Remove any old response.
         *
         * A new request must produce a new response ID.
         */
        rewardedResponseId = null
        rewardedResponseAlreadyDelivered = null

        Log.d(
            TAG,
            "Requesting rewarded video. Zone=$targetZone"
        )

        try {

            /*
             * This matches the official Android sample:
             *
             * TapsellPlus.requestRewardedVideoAd(
             *     activity,
             *     zoneId,
             *     callback
             * )
             *
             * The Activity is supplied later through
             * requestRewardedVideoFromActivity().
             *
             * Since the AdManager interface does not currently
             * provide an Activity parameter, this method cannot
             * directly call the SDK request method.
             *
             * Therefore the actual request is performed by
             * requestRewardedVideoFromActivity().
             */

            listener?.onAdFailedToLoad(
                "Tapsell rewarded request requires an Activity. " +
                        "Use requestRewardedVideoFromActivity()."
            )

        } catch (exception: Exception) {

            val errorMessage =
                exception.message
                    ?: "Unknown Tapsell request error."

            Log.e(
                TAG,
                "Tapsell request exception.",
                exception
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )
        }
    }

    /**
     * Requests a real rewarded video using the Activity required
     * by Tapsell Plus SDK.
     *
     * This is the real SDK request path.
     */
    fun requestRewardedVideoFromActivity(
        activity: Activity,
        zoneId: String = rewardedZoneId,
        listener: RewardedAdListener? = null
    ) {
        if (!isConfigured()) {

            val errorMessage =
                "Tapsell App Key is not configured."

            Log.e(
                TAG,
                errorMessage
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )

            return
        }

        if (!initialized) {

            val errorMessage =
                "Tapsell SDK is not initialized."

            Log.e(
                TAG,
                errorMessage
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )

            return
        }

        if (activity.isFinishing) {

            val errorMessage =
                "Activity is finishing."

            Log.e(
                TAG,
                errorMessage
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )

            return
        }

        val targetZone =
            if (zoneId.isNotBlank()) {
                zoneId
            } else {
                rewardedZoneId
            }

        if (
            targetZone.isBlank() ||
            targetZone.contains(
                "YOUR_TAPSELL_REWARDED_ZONE_ID",
                ignoreCase = true
            )
        ) {

            val errorMessage =
                "Tapsell rewarded Zone ID is not configured."

            Log.e(
                TAG,
                errorMessage
            )

            listener?.onAdFailedToLoad(
                errorMessage
            )

            return
        }

        rewardedResponseId = null
        rewardedResponseAlreadyDelivered = null

        try {

            TapsellPlus.requestRewardedVideoAd(
                activity,
                targetZone,
                object : AdRequestCallback() {

                    override fun response(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.response(
                            tapsellPlusAdModel
                        )

                        if (activity.isFinishing) {
                            return
                        }

                        val responseId =
                            tapsellPlusAdModel.getResponseId()

                        if (responseId.isBlank()) {

                            val errorMessage =
                                "Tapsell returned an empty response ID."

                            Log.e(
                                TAG,
                                errorMessage
                            )

                            listener?.onAdFailedToLoad(
                                errorMessage
                            )

                            return
                        }

                        rewardedResponseId =
                            responseId

                        Log.i(
                            TAG,
                            "Rewarded video loaded. " +
                                    "Response ID received."
                        )

                        listener?.onAdLoaded()
                    }

                    override fun error(
                        @NonNull message: String
                    ) {
                        rewardedResponseId = null

                        Log.e(
                            TAG,
                            "Rewarded video request failed: $message"
                        )

                        listener?.onAdFailedToLoad(
                            message
                        )
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

            listener?.onAdFailedToLoad(
                errorMessage
            )
        }
    }

    // -------------------------------------------------------------------------
    // Show rewarded video
    // -------------------------------------------------------------------------

    override fun showRewardedVideo(
        activity: Activity,
        zoneId: String,
        listener: RewardedAdListener
    ) {
        if (!isConfigured()) {

            val errorMessage =
                "Tapsell App Key is not configured."

            Log.e(
                TAG,
                errorMessage
            )

            listener.onAdShowFailed(
                errorMessage
            )

            return
        }

        if (!initialized) {

            val errorMessage =
                "Tapsell SDK is not initialized."

            Log.e(
                TAG,
                errorMessage
            )

            listener.onAdShowFailed(
                errorMessage
            )

            return
        }

        if (activity.isFinishing) {

            val errorMessage =
                "Activity is finishing."

            Log.e(
                TAG,
                errorMessage
            )

            listener.onAdShowFailed(
                errorMessage
            )

            return
        }

        val responseId =
            rewardedResponseId

        if (responseId.isNullOrBlank()) {

            val errorMessage =
                "No rewarded video is ready."

            Log.e(
                TAG,
                errorMessage
            )

            listener.onAdShowFailed(
                errorMessage
            )

            return
        }

        /*
         * Consume response immediately.
         *
         * The same response ID must never be shown twice.
         */
        rewardedResponseId = null

        Log.i(
            TAG,
            "Showing real Tapsell rewarded video."
        )

        try {

            TapsellPlus.showRewardedVideoAd(
                activity,
                responseId,
                object : AdShowListener() {

                    override fun onOpened(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.onOpened(
                            tapsellPlusAdModel
                        )

                        Log.d(
                            TAG,
                            "Rewarded video opened."
                        )

                        listener.onAdOpened()
                    }

                    override fun onClosed(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.onClosed(
                            tapsellPlusAdModel
                        )

                        val rewardWasDelivered =
                            rewardedResponseAlreadyDelivered ==
                                    responseId

                        Log.d(
                            TAG,
                            "Rewarded video closed. " +
                                    "Reward delivered=$rewardWasDelivered"
                        )

                        /*
                         * IMPORTANT:
                         * NEVER grant the reward here.
                         */
                        listener.onAdClosed(
                            rewardCompleted =
                                rewardWasDelivered
                        )
                    }

                    override fun onRewarded(
                        tapsellPlusAdModel: TapsellPlusAdModel
                    ) {
                        super.onRewarded(
                            tapsellPlusAdModel
                        )

                        /*
                         * Duplicate protection.
                         */
                        if (
                            rewardedResponseAlreadyDelivered ==
                            responseId
                        ) {

                            Log.w(
                                TAG,
                                "Duplicate reward callback ignored."
                            )

                            return
                        }

                        /*
                         * This is the ONLY callback that marks
                         * the ad as reward-verified.
                         */
                        rewardedResponseAlreadyDelivered =
                            responseId

                        Log.i(
                            TAG,
                            "REAL TAPSELL REWARD VERIFIED."
                        )

                        listener.onRewardEarned(
                            rewardAmount = REWARD_COINS
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
                            "Rewarded video error: $errorMessage"
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

    // -------------------------------------------------------------------------
    // Banner
    // -------------------------------------------------------------------------

    override fun requestNativeBanner(
        zoneId: String,
        listener: NativeAdListener
    ) {
        /*
         * The previous implementation generated fake banner data.
         *
         * That behavior has intentionally been removed.
         * A real Tapsell banner integration will be added separately.
         */
        val errorMessage =
            "Fake native banner disabled. " +
                    "Real Tapsell banner integration is required."

        Log.w(
            TAG,
            errorMessage
        )

        listener.onAdFailed(
            errorMessage
        )
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    fun clearRewardedAd() {
        rewardedResponseId = null
        rewardedResponseAlreadyDelivered = null
    }

    private fun maskKey(
        key: String
    ): String {
        return if (key.length > 8) {
            "${key.take(4)}...${key.takeLast(4)}"
        } else {
            "***"
        }
    }
}
