package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CrosswordLevel
import com.example.data.GameLevelsData
import com.example.data.UserEntity
import com.example.ui.CrosswordGameState
import com.example.ui.components.ConfettiOverlay
import com.example.ui.theme.GameTheme
import com.example.ui.theme.GameThemes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CrosswordGameScreen(
    state: CrosswordGameState,
    user: UserEntity?,
    theme: GameTheme = GameThemes.TURQUOISE,
    onSelectCell: (Int, Int) -> Unit,
    onInputChar: (Char) -> Unit,
    onClearCell: () -> Unit,
    onUseHint: () -> Unit,
    onNextLevel: () -> Unit,
    onDismissWinDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level: CrosswordLevel = GameLevelsData.crosswordLevels.getOrElse(state.currentLevelIndex) {
        GameLevelsData.crosswordLevels.first()
    }

    // Common Persian letters keyboard for easy mobile entry
    val persianAlphabet = listOf(
        'ا', 'ب', 'پ', 'ت', 'ث', 'ج', 'چ', 'ح', 'خ',
        'د', 'ذ', 'ر', 'ز', 'ژ', 'س', 'ش', 'ص', 'ض',
        'ط', 'ظ', 'ع', 'غ', 'ف', 'ق', 'ک', 'گ', 'ل',
        'م', 'ن', 'و', 'ه', 'ی', 'آ'
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crossword Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = level.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = "جایزه: ${if (user?.isVip == true) level.coinReward * 2 else level.coinReward} سکه",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Crossword Grid Container
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (r in 0 until level.rows) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (c in 0 until level.cols) {
                            val activeCell = level.cells.find { it.row == r && it.col == c }
                            val isSelected = state.selectedCell?.let { it.first == r && it.second == c } ?: false
                            val enteredChar = state.enteredGrid[r to c]

                            if (activeCell != null) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) Color(0xFFFFE082)
                                            else if (enteredChar != null && enteredChar == activeCell.correctChar) Color(0xFFC8E6C9)
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color(0xFFFF8F00) else MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onSelectCell(r, c) }
                                        .testTag("crossword_cell_${r}_$c"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Small clue number in corner
                                    if (activeCell.clueNumber != null) {
                                        Text(
                                            text = "${activeCell.clueNumber}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(2.dp)
                                        )
                                    }

                                    // Entered Letter
                                    Text(
                                        text = enteredChar?.toString() ?: "",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (enteredChar == activeCell.correctChar) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            } else {
                                // Blocked Cell
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clues Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "راهنمای سوالات جدول:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                level.clues.forEach { clue ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (clue.isAcross) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = if (clue.isAcross) "افقی ${clue.number}" else "عمودی ${clue.number}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${clue.clueText} (${clue.answer.length} حرفی)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hint and Clear Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onUseHint,
                enabled = state.selectedCell != null,
                modifier = Modifier.testTag("crossword_hint_btn")
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (user?.isVip == true) "راهنمای رایگان VIP" else "راهنما (۲۰ سکه)", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onClearCell,
                enabled = state.selectedCell != null,
                modifier = Modifier.testTag("crossword_clear_cell_btn")
            ) {
                Icon(Icons.Default.Backspace, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("پاک کردن خانه", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Virtual Persian Keyboard
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "کیبورد سریع حروف فارسی:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    persianAlphabet.forEach { char ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onInputChar(char) }
                                .testTag("crossword_key_$char")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = char.toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Win Dialog
        if (state.showWinDialog) {
            ConfettiOverlay()
            AlertDialog(
                onDismissRequest = onDismissWinDialog,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جدول با موفقیت حل شد!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                },
                text = {
                    Text(
                        text = "آفرین! شما این جدول کلمات را کامل کردید.\n" +
                                if (user?.isVip == true)
                                    "+${level.coinReward * 2} سکه طلایی VIP دریافت کردید!"
                                else
                                    "+${level.coinReward} سکه به کیف پول شما اضافه شد.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDismissWinDialog()
                            onNextLevel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("جدول بعدی")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                    }
                }
            )
        }
    }
}
