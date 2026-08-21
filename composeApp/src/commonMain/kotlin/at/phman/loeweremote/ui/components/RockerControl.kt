package at.phman.loeweremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicMuted
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicSurfaceDark
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated
import at.phman.loeweremote.ui.theme.AnthropicTerracotta
import at.phman.loeweremote.ui.theme.AnthropicTerracottaBg
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun RockerControl(
    onKeyClick: (LoeweKey) -> Unit,
    onVolumeStep: (delta: Int) -> Unit,
    volume: Int?,
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
        // VOLUME ROCKER (Greek Stele / Column Style with live volume readout & continuous long-press hold)
        VolumeRocker(
            volume = volume,
            height = rockerHeight,
            width = rockerWidth,
            onPlusStep = { onVolumeStep(1) },
            onMinusStep = { onVolumeStep(-1) }
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
        ProgramRocker(
            label = "PROG",
            height = rockerHeight,
            width = rockerWidth,
            onPlusClick = { onKeyClick(LoeweKey.PROGRAM_UP) },
            onMinusClick = { onKeyClick(LoeweKey.PROGRAM_DOWN) }
        )
    }
}

@Composable
private fun VolumeRocker(
    volume: Int?,
    height: Dp,
    width: Dp,
    onPlusStep: () -> Unit,
    onMinusStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val symbolSize = (height.value * 0.18f).coerceIn(18f, 24f).sp

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
        // PLUS (+) with continuous hold repeat
        RockerHoldButton(
            symbol = "+",
            fontSize = symbolSize,
            onStep = onPlusStep,
            modifier = Modifier.weight(1f)
        )

        // CENTER READOUT (Live numerical volume or classical VOL label)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (volume != null) {
                Text(
                    text = "$volume",
                    color = AnthropicParchment,
                    fontSize = (height.value * 0.14f).coerceIn(13f, 17f).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "VOL",
                    color = AnthropicMuted.copy(alpha = 0.65f),
                    fontSize = (height.value * 0.055f).coerceIn(7f, 9f).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            } else {
                Text(
                    text = "VOL",
                    color = AnthropicMuted,
                    fontSize = (height.value * 0.08f).coerceIn(9f, 11f).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // MINUS (−) with continuous hold repeat
        RockerHoldButton(
            symbol = "−",
            fontSize = symbolSize,
            onStep = onMinusStep,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RockerHoldButton(
    symbol: String,
    fontSize: TextUnit,
    onStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val currentOnStep by rememberUpdatedState(onStep)

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(320) // Initial hold threshold before continuous ramp
            while (isActive && isPressed) {
                currentOnStep()
                delay(120) // Continuous tick interval
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        currentOnStep() // Instant step on initial touch
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = AnthropicParchment,
            fontSize = fontSize,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun ProgramRocker(
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

        // CENTER LABEL
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
