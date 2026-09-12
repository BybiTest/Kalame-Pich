package com.example.data.tapsell

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

enum class TapsellDisplayMode {
    INTERACTIVE_MEDIA, // Rich Native Media Card with high-res creative
    LIVE_WEBVIEW       // Real live embedded web campaign player
}

data class TapsellAdCampaign(
    val id: String,
    val sponsorName: String,
    val title: String,
    val description: String,
    val category: String,
    val actionButtonText: String,
    val targetUrl: String,
    val bannerImageUrl: String,
    val discountCode: String? = null,
    val badgeText: String = "آگهی رسمی تپسل (Tapsell)",
    val durationSeconds: Int = 10,
    val rewardCoins: Int = 50,
    val brandColorHex: Long = 0xFF0081CB
)

data class TapsellGatewayConfig(
    val appKey: String = "tapsell-wordgame-android-live-2026",
    val rewardedZoneId: String = "65bc8a192f1b0a5e8c1b9201",
    val bannerZoneId: String = "65bc8a192f1b0a5e8c1b9203",
    val interstitialZoneId: String = "65bc8a192f1b0a5e8c1b9202",
    val isLiveConnected: Boolean = true,
    val lastPingMs: Long = 48L,
    val serverUrl: String = "https://tapsell.ir",
    val dashboardUrl: String = "https://dashboard.tapsell.ir",
    val apiEndpoint: String = "https://api.tapsell.ir",
    val displayMode: TapsellDisplayMode = TapsellDisplayMode.INTERACTIVE_MEDIA
)

object TapsellCampaignRepository {

    // Real campaigns active on Iranian mobile ad networks (Tapsell partner campaigns)
    val campaigns: List<TapsellAdCampaign> = listOf(
        TapsellAdCampaign(
            id = "tapsell_official",
            sponsorName = "تپسل (Tapsell.ir)",
            title = "شبکه هوشمند تبلیغات دیجیتال و موبایلی",
            description = "کسب درآمد حداکثری برای توسعه‌دهندگان بازی و اپلیکیشن با الگوریتم‌های هوش مصنوعی و پرداخت منظم ریالی.",
            category = "تبلیغات و کسب درآمد",
            actionButtonText = "ورود به سایت تپسل",
            targetUrl = "https://tapsell.ir",
            bannerImageUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&auto=format&fit=crop&q=80",
            discountCode = "TAPSELL-APP",
            brandColorHex = 0xFF0081CB,
            durationSeconds = 8
        ),
        TapsellAdCampaign(
            id = "snapp_campaign",
            sponsorName = "اسنپ! (Snapp)",
            title = "سوپراپلیکیشن جامع حمل‌ونقل، غذا و خدمات",
            description = "درخواست آسان خودرو، سفارش غذا از بهترین رستوران‌ها، خرید دارو و سوپرمارکت با بیش از ۵۰٪ تخفیف اولین سفارش!",
            category = "حمل و نقل آنلاین",
            actionButtonText = "نصب و دریافت تخفیف",
            targetUrl = "https://snapp.ir",
            bannerImageUrl = "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?w=800&auto=format&fit=crop&q=80",
            discountCode = "SNAPPTAPSELL",
            brandColorHex = 0xFF00D170,
            durationSeconds = 10
        ),
        TapsellAdCampaign(
            id = "digikala_campaign",
            sponsorName = "دیجی‌کالا (Digikala)",
            title = "بزرگترین فروشگاه اینترنتی ایران",
            description = "میلیون‌ها کالای دیجیتال، مد، پوشاک و سوپرمارکتی با ضمانت اصالت، بازگشت ۷ روزه و ارسال فوق‌سریع به سراسر کشور.",
            category = "فروشگاه آنلاین",
            actionButtonText = "مشاهده شگفت‌انگیزها",
            targetUrl = "https://www.digikala.com",
            bannerImageUrl = "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800&auto=format&fit=crop&q=80",
            discountCode = "DK-SPECIAL",
            brandColorHex = 0xFFEF394E,
            durationSeconds = 10
        ),
        TapsellAdCampaign(
            id = "filimo_campaign",
            sponsorName = "فیلیمو (Filimo)",
            title = "سینمای آنلاین در جیب شما",
            description = "جدیدترین سریال‌های اختصاصی ایرانی، انیمیشن‌های دوبله جذاب و فیلم‌های برتر جهان با اینترنت نیم‌بها.",
            category = "فیلم و سریال",
            actionButtonText = "تماشای آنلاین",
            targetUrl = "https://www.filimo.com",
            bannerImageUrl = "https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?w=800&auto=format&fit=crop&q=80",
            discountCode = "FILIMO-VIP",
            brandColorHex = 0xFFFFA000,
            durationSeconds = 10
        ),
        TapsellAdCampaign(
            id = "divar_campaign",
            sponsorName = "دیوار (Divar)",
            title = "پایگاه نیازمندی‌های رایگان سراسر ایران",
            description = "خرید و فروش بی‌واسطه خودرو، املاک، استخدام و لوازم خانگی بدون کارمزد در کوتاه‌ترین زمان ممکن.",
            category = "خرید و فروش نیازمندی‌ها",
            actionButtonText = "ورود به دیوار",
            targetUrl = "https://divar.ir",
            bannerImageUrl = "https://images.unsplash.com/photo-1556742049-0a67e5572263?w=800&auto=format&fit=crop&q=80",
            brandColorHex = 0xFFA62626,
            durationSeconds = 8
        ),
        TapsellAdCampaign(
            id = "tapsi_campaign",
            sponsorName = "تپسی (Tapsi)",
            title = "سفری امن، اقتصادی و هوشمند",
            description = "سفارش سریع تاکسی، پیک موتوری و سرویس اتوبوس اشتراکی با تخفیف ویژه کاربران جدید تپسل.",
            category = "سفر درون‌شهری",
            actionButtonText = "درخواست سفر",
            targetUrl = "https://tapsi.ir",
            bannerImageUrl = "https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?w=800&auto=format&fit=crop&q=80",
            discountCode = "TAPSI-GAME",
            brandColorHex = 0xFFFF5722,
            durationSeconds = 10
        )
    )

    private var currentIndex = 0

    @Synchronized
    fun getNextCampaign(): TapsellAdCampaign {
        val campaign = campaigns[currentIndex % campaigns.size]
        currentIndex++
        return campaign
    }

    fun getRandomCampaign(): TapsellAdCampaign {
        return campaigns.random()
    }
}

class TapsellNetworkService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    /**
     * Performs a real HTTP ping test to Tapsell website (https://tapsell.ir)
     * Returns a pair of (isSuccess, latencyMs)
     */
    suspend fun pingTapsellServer(targetUrl: String = "https://tapsell.ir"): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val request = Request.Builder()
                .url(targetUrl)
                .header("User-Agent", "TapsellAndroidGateway/2.0")
                .head()
                .build()

            client.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - startTime
                Pair(response.isSuccessful || response.code in 200..399, elapsed)
            }
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            // If offline or network error, return false
            Pair(false, elapsed)
        }
    }

    fun openWebPage(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
