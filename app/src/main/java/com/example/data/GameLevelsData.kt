package com.example.data

data class WordLevel(
    val id: Int,
    val title: String,
    val theme: String,
    val letters: List<Char>,
    val targetWords: List<String>,
    val bonusWords: List<String> = emptyList(),
    val coinReward: Int = 25
)

data class CrosswordCell(
    val row: Int,
    val col: Int,
    val correctChar: Char,
    val clueNumber: Int? = null
)

data class CrosswordClue(
    val number: Int,
    val isAcross: Boolean,
    val clueText: String,
    val answer: String,
    val startRow: Int,
    val startCol: Int
)

data class CrosswordLevel(
    val id: Int,
    val title: String,
    val rows: Int,
    val cols: Int,
    val clues: List<CrosswordClue>,
    val cells: List<CrosswordCell>,
    val coinReward: Int = 40
)

object GameLevelsData {
    val wordLevels = listOf(
        WordLevel(
            id = 1,
            title = "مرحله ۱",
            theme = "فصل بهار",
            letters = listOf('ب', 'ا', 'ر', 'ه'),
            targetWords = listOf("بهار", "راه", "ابر", "بار"),
            bonusWords = listOf("بهر", "ربا"),
            coinReward = 20
        ),
        WordLevel(
            id = 2,
            title = "مرحله ۲",
            theme = "طبیعت و دریا",
            letters = listOf('د', 'ر', 'ی', 'ا'),
            targetWords = listOf("دریا", "دیر", "یار", "رای"),
            bonusWords = listOf("دید"),
            coinReward = 25
        ),
        WordLevel(
            id = 3,
            title = "مرحله ۳",
            theme = "کتابخانه و دانش",
            letters = listOf('ک', 'ت', 'ا', 'ب'),
            targetWords = listOf("کتاب", "باک", "تاب", "کاب"),
            bonusWords = listOf("بات"),
            coinReward = 25
        ),
        WordLevel(
            id = 4,
            title = "مرحله ۴",
            theme = "آسمان شب",
            letters = listOf('س', 'ت', 'ا', 'ر', 'ه'),
            targetWords = listOf("ستاره", "راست", "تار", "ساز", "سرا"),
            bonusWords = listOf("ترس", "تاس"),
            coinReward = 30
        ),
        WordLevel(
            id = 5,
            title = "مرحله ۵",
            theme = "خانه و خانواده",
            letters = listOf('م', 'ا', 'د', 'ر'),
            targetWords = listOf("مادر", "آرد", "دام", "مار", "رام"),
            bonusWords = listOf("درام"),
            coinReward = 30
        ),
        WordLevel(
            id = 6,
            title = "مرحله ۶",
            theme = "گل و گیاه",
            letters = listOf('گ', 'ل', 'د', 'ا', 'ن'),
            targetWords = listOf("گلدان", "لگد", "لگن", "دانا", "گدا"),
            bonusWords = listOf("انگ", "دنگ"),
            coinReward = 35
        ),
        WordLevel(
            id = 7,
            title = "مرحله ۷",
            theme = "خورشید تابان",
            letters = listOf('خ', 'و', 'ر', 'ش', 'ی', 'د'),
            targetWords = listOf("خورشید", "روشن", "شیر", "دوش", "خرد"),
            bonusWords = listOf("رود", "شور"),
            coinReward = 40
        ),
        WordLevel(
            id = 8,
            title = "مرحله ۸",
            theme = "مهربانی و دوستی",
            letters = listOf('م', 'ه', 'ر', 'ب', 'ا', 'ن'),
            targetWords = listOf("مهربان", "بهار", "ماهر", "انبار", "بام"),
            bonusWords = listOf("مهر", "نام", "نرم"),
            coinReward = 50
        )
    )

    val crosswordLevels = listOf(
        CrosswordLevel(
            id = 1,
            title = "جدول ۱: طبیعت و زندگی",
            rows = 4,
            cols = 4,
            clues = listOf(
                CrosswordClue(
                    number = 1,
                    isAcross = true,
                    clueText = "مایع حیات و باران",
                    answer = "آب",
                    startRow = 0,
                    startCol = 0
                ),
                CrosswordClue(
                    number = 2,
                    isAcross = true,
                    clueText = "فصل شکوفه و نوروز",
                    answer = "بهار",
                    startRow = 1,
                    startCol = 0
                ),
                CrosswordClue(
                    number = 3,
                    isAcross = true,
                    clueText = "مسیر عبور و مرور",
                    answer = "راه",
                    startRow = 3,
                    startCol = 1
                ),
                CrosswordClue(
                    number = 4,
                    isAcross = false,
                    clueText = "میوه ابری باران‌زا",
                    answer = "ابر",
                    startRow = 0,
                    startCol = 0
                )
            ),
            cells = listOf(
                CrosswordCell(0, 0, 'آ', clueNumber = 1),
                CrosswordCell(0, 1, 'ب'),
                CrosswordCell(1, 0, 'ب', clueNumber = 2),
                CrosswordCell(1, 1, 'ه'),
                CrosswordCell(1, 2, 'ا'),
                CrosswordCell(1, 3, 'ر'),
                CrosswordCell(2, 0, 'ر'),
                CrosswordCell(3, 1, 'ر', clueNumber = 3),
                CrosswordCell(3, 2, 'ا'),
                CrosswordCell(3, 3, 'ه')
            ),
            coinReward = 45
        ),
        CrosswordLevel(
            id = 2,
            title = "جدول ۲: ایران و فرهنگ",
            rows = 4,
            cols = 4,
            clues = listOf(
                CrosswordClue(
                    number = 1,
                    isAcross = true,
                    clueText = "نام کشور عزیزمان",
                    answer = "ایران",
                    startRow = 0,
                    startCol = 0
                ),
                CrosswordClue(
                    number = 2,
                    isAcross = true,
                    clueText = "برکت سفره و خوراک اصلی",
                    answer = "نان",
                    startRow = 2,
                    startCol = 0
                ),
                CrosswordClue(
                    number = 3,
                    isAcross = false,
                    clueText = "روشنایی مقابل تاریکی",
                    answer = "نور",
                    startRow = 1,
                    startCol = 2
                )
            ),
            cells = listOf(
                CrosswordCell(0, 0, 'ا', clueNumber = 1),
                CrosswordCell(0, 1, 'ی'),
                CrosswordCell(0, 2, 'ر'),
                CrosswordCell(0, 3, 'ن'),
                CrosswordCell(1, 2, 'ن', clueNumber = 3),
                CrosswordCell(2, 0, 'ن', clueNumber = 2),
                CrosswordCell(2, 1, 'ا'),
                CrosswordCell(2, 2, 'ن'),
                CrosswordCell(3, 2, 'ر')
            ),
            coinReward = 50
        )
    )
}
