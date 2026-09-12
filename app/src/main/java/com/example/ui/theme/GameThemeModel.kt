package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class GameTheme(
    val id: String,
    val name: String,
    val subtitle: String,
    val icon: String,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val accent: Color,
    val backgroundGradient: Brush,
    val cardBackground: Color,
    val cardBorder: Color,
    val textColor: Color,
    val textSecondary: Color,
    val letterWheelCenter: Color,
    val letterButtonGradient: List<Color>,
    val isDark: Boolean = false
)

object GameThemes {
    val TURQUOISE = GameTheme(
        id = "turquoise",
        name = "فیروزه نیشابور",
        subtitle = "اصالت ایرانی و آرامش فیروزه‌ای",
        icon = "💎",
        primary = Color(0xFF00838F),
        primaryVariant = Color(0xFF00ACC1),
        secondary = Color(0xFF004D40),
        accent = Color(0xFFFFB300),
        backgroundGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFFE8F5E9))
        ),
        cardBackground = Color(0xFFFFFFFF),
        cardBorder = Color(0xFF80DEEA),
        textColor = Color(0xFF004D40),
        textSecondary = Color(0xFF00695C),
        letterWheelCenter = Color(0xFFE0F2F1),
        letterButtonGradient = listOf(Color(0xFF0097A7), Color(0xFF00838F))
    )

    val COSMIC = GameTheme(
        id = "cosmic",
        name = "کهکشان شب",
        subtitle = "سکوت پررمزوراز آسمان پرستاره",
        icon = "🌌",
        primary = Color(0xFF7C4DFF),
        primaryVariant = Color(0xFF651FFF),
        secondary = Color(0xFF00E5FF),
        accent = Color(0xFFFFD700),
        backgroundGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042))
        ),
        cardBackground = Color(0xFF1E293B),
        cardBorder = Color(0xFF6366F1),
        textColor = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        letterWheelCenter = Color(0xFF1E1B4B),
        letterButtonGradient = listOf(Color(0xFF7C4DFF), Color(0xFF4A148C)),
        isDark = true
    )

    val DESERT = GameTheme(
        id = "desert",
        name = "غروب کویر و زعفران",
        subtitle = "گرمای دلنشین کهربا و معماری خشتی",
        icon = "🏜️",
        primary = Color(0xFFD84315),
        primaryVariant = Color(0xFFEF6C00),
        secondary = Color(0xFFFF8F00),
        accent = Color(0xFFFFB300),
        backgroundGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFE0B2), Color(0xFFFFCCBC))
        ),
        cardBackground = Color(0xFFFFFFFF),
        cardBorder = Color(0xFFFFCC80),
        textColor = Color(0xFF4E342E),
        textSecondary = Color(0xFF8D6E63),
        letterWheelCenter = Color(0xFFFFF3E0),
        letterButtonGradient = listOf(Color(0xFFE64A19), Color(0xFFD84315))
    )

    val ROYAL = GameTheme(
        id = "royal",
        name = "کاخ سلطنتی زرین",
        subtitle = "جلوه شاهانه طلا و شکوهمندی",
        icon = "👑",
        primary = Color(0xFFFFD700),
        primaryVariant = Color(0xFFFFC107),
        secondary = Color(0xFFFFA000),
        accent = Color(0xFFFFE082),
        backgroundGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF18181B), Color(0xFF27272A), Color(0xFF1C1917))
        ),
        cardBackground = Color(0xFF27272A),
        cardBorder = Color(0xFFFFD700),
        textColor = Color(0xFFFFFBEB),
        textSecondary = Color(0xFFD4D4D8),
        letterWheelCenter = Color(0xFF3F3F46),
        letterButtonGradient = listOf(Color(0xFFFFB300), Color(0xFFFF8F00)),
        isDark = true
    )

    val allThemes = listOf(TURQUOISE, COSMIC, DESERT, ROYAL)

    fun getThemeById(id: String?): GameTheme {
        return allThemes.find { it.id == id } ?: TURQUOISE
    }
}
