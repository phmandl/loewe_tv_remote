package at.phman.loeweremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicGoldBorder
import at.phman.loeweremote.ui.theme.AnthropicMuted
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicSurfaceDark
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated
import at.phman.loeweremote.ui.theme.AnthropicTerracotta
import at.phman.loeweremote.ui.theme.AnthropicTerracottaBg

@Composable
fun RockerControl(
    onKeyClick: (LoeweKey) -> Unit,
    onToggleNumpad: () -> Unit,
    isNumpadExpanded: Boolean,
    modifier: Modifier = Modifier,
    rockerHeight: Dp = 120.dp
) {
    val rockerWidth = (rockerHeight * 0.52f).coerceIn(56.dp, 66.dp)
    val centerSpacing = 8.dp
    // EPG and 123 buttons in sum with their spacing match the exact height and width of Vol/Prog:
    val centerBtnHeight = (rockerHeight - centerSpacing) / 2
    val centerBtnWidth = rockerWidth

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // VOLUME ROCKER (Greek Stele / Column Style)
        VerticalRocker(
            label = "VOL",
            height = rockerHeight,
            width = rockerWidth,
            onPlusClick = { onKeyClick(LoeweKey.VOLUME_UP) },
            onMinusClick = { onKeyClick(LoeweKey.VOLUME_DOWN) }
        )

        // CENTER QUICK ACCESS (EPG & Numpad toggle - Exact sum height & same width as rockers)
        Column(
            modifier = Modifier.height(rockerHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // EPG / GUIDE
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.EPG) },
                shape = RoundedCornerShape(18.dp),
                backgroundColor = AnthropicSurfaceElevated,
                borderColor = AnthropicBorder,
                minSize = centerBtnHeight,
                modifier = Modifier.size(width = centerBtnWidth, height = centerBtnHeight)
            ) {
                Text(
                    text = "EPG",
                    color = AnthropicSand,
                    fontSize = (centerBtnHeight.value * 0.28f).coerceIn(12f, 15f).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            // 1 2 3 TOGGLE NUMPAD
            RemoteButton(
                onClick = onToggleNumpad,
                shape = RoundedCornerShape(18.dp),
                minSize = centerBtnHeight,
                borderColor = if (isNumpadExpanded) AnthropicTerracotta else AnthropicBorder,
                backgroundColor = if (isNumpadExpanded) AnthropicTerracottaBg else AnthropicSurfaceElevated,
                modifier = Modifier.size(width = centerBtnWidth, height = centerBtnHeight)
            ) {
                Text(
                    text = "1 2 3",
                    color = if (isNumpadExpanded) AnthropicTerracotta else AnthropicParchment,
                    fontSize = (centerBtnHeight.value * 0.28f).coerceIn(12f, 15f).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // PROGRAM / CHANNEL ROCKER
        VerticalRocker(
            label = "PROG",
            height = rockerHeight,
            width = rockerWidth,
            onPlusClick = { onKeyClick(LoeweKey.PROGRAM_UP) },
            onMinusClick = { onKeyClick(LoeweKey.PROGRAM_DOWN) }
        )
    }
}

@Composable
private fun VerticalRocker(
    label: String,
    height: Dp,
    width: Dp,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AnthropicSurfaceElevated,
                        AnthropicSurfaceDark
                    )
                )
            )
            .border(1.2.dp, AnthropicBorder, RoundedCornerShape(26.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // PLUS (+)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(onClick = onPlusClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = AnthropicParchment,
                fontSize = (height.value * 0.18f).coerceIn(18f, 24f).sp,
                fontWeight = FontWeight.Light
            )
        }

        // CENTER LABEL (Classical Serif Pillar Inscription)
        Text(
            text = label,
            color = AnthropicMuted,
            fontSize = (height.value * 0.08f).coerceIn(9f, 11f).sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        // MINUS (−)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(onClick = onMinusClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                color = AnthropicParchment,
                fontSize = (height.value * 0.18f).coerceIn(18f, 24f).sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

