package at.phman.loeweremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicSurfaceDark
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated
import at.phman.loeweremote.ui.theme.AnthropicSurfaceMedallion
import at.phman.loeweremote.ui.theme.AnthropicTerracotta

enum class DPadDirection { UP, DOWN, LEFT, RIGHT }

@Composable
fun DPadArrow(
    direction: DPadDirection,
    color: Color = AnthropicParchment,
    size: Dp = 14.dp
) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            when (direction) {
                DPadDirection.UP -> {
                    moveTo(w / 2f, 0f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                DPadDirection.DOWN -> {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w / 2f, h)
                    close()
                }
                DPadDirection.LEFT -> {
                    moveTo(0f, h / 2f)
                    lineTo(w, 0f)
                    lineTo(w, h)
                    close()
                }
                DPadDirection.RIGHT -> {
                    moveTo(0f, 0f)
                    lineTo(w, h / 2f)
                    lineTo(0f, h)
                    close()
                }
            }
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun DPadControl(
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier,
    dpadSize: Dp = 200.dp
) {
    // Proportional dimensions based on dynamically provided dpadSize
    val okSize = (dpadSize * 0.36f).coerceIn(52.dp, 80.dp)
    val vertBtnWidth = (dpadSize * 0.32f).coerceIn(48.dp, 72.dp)
    val vertBtnHeight = (dpadSize * 0.24f).coerceIn(36.dp, 52.dp)
    val horizBtnWidth = (dpadSize * 0.24f).coerceIn(36.dp, 52.dp)
    val horizBtnHeight = (dpadSize * 0.32f).coerceIn(48.dp, 72.dp)
    val arrowIconSize = (dpadSize * 0.075f).coerceIn(12.dp, 16.dp)
    val okFontSize = (dpadSize.value * 0.085f).coerceIn(14f, 20f).sp
    val navBtnHeight = (dpadSize * 0.20f).coerceIn(34.dp, 44.dp)
    val navFontSize = (dpadSize.value * 0.055f).coerceIn(10f, 12.5f).sp

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // D-Pad Outer Frame
        Box(
            modifier = Modifier
                .size(dpadSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AnthropicSurfaceMedallion,
                            AnthropicSurfaceElevated,
                            AnthropicSurfaceDark
                        )
                    )
                )
                .border(1.dp, AnthropicBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {

            // UP BUTTON
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (dpadSize * 0.035f))
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.UP) },
                    shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    minSize = 36.dp,
                    modifier = Modifier.size(width = vertBtnWidth, height = vertBtnHeight)
                ) {
                    DPadArrow(direction = DPadDirection.UP, color = AnthropicParchment, size = arrowIconSize)
                }
            }

            // DOWN BUTTON
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = (dpadSize * 0.035f))
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.DOWN) },
                    shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp, topStart = 8.dp, topEnd = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    minSize = 36.dp,
                    modifier = Modifier.size(width = vertBtnWidth, height = vertBtnHeight)
                ) {
                    DPadArrow(direction = DPadDirection.DOWN, color = AnthropicParchment, size = arrowIconSize)
                }
            }

            // LEFT BUTTON
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = (dpadSize * 0.035f))
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.LEFT) },
                    shape = RoundedCornerShape(topStart = 36.dp, bottomStart = 36.dp, topEnd = 8.dp, bottomEnd = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    minSize = 36.dp,
                    modifier = Modifier.size(width = horizBtnWidth, height = horizBtnHeight)
                ) {
                    DPadArrow(direction = DPadDirection.LEFT, color = AnthropicParchment, size = arrowIconSize)
                }
            }

            // RIGHT BUTTON
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = (dpadSize * 0.035f))
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.RIGHT) },
                    shape = RoundedCornerShape(topEnd = 36.dp, bottomEnd = 36.dp, topStart = 8.dp, bottomStart = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    minSize = 36.dp,
                    modifier = Modifier.size(width = horizBtnWidth, height = horizBtnHeight)
                ) {
                    DPadArrow(direction = DPadDirection.RIGHT, color = AnthropicParchment, size = arrowIconSize)
                }
            }

            // CENTER OK BUTTON (Ancient Terracotta Seal Medallion)
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.OK) },
                shape = CircleShape,
                backgroundColor = AnthropicSurfaceElevated,
                borderColor = AnthropicTerracotta.copy(alpha = 0.85f),
                minSize = okSize,
                modifier = Modifier.size(okSize)
            ) {
                Text(
                    text = "OK",
                    color = AnthropicParchment,
                    fontSize = okFontSize,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height((dpadSize * 0.045f).coerceIn(4.dp, 10.dp)))

        // Back, End & Home Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK BUTTON
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.BACK) },
                shape = RoundedCornerShape(12.dp),
                backgroundColor = AnthropicSurfaceElevated,
                borderColor = AnthropicBorder,
                minSize = navBtnHeight,
                fontSize = navFontSize,
                modifier = Modifier
                    .weight(1f)
                    .height(navBtnHeight)
            ) {
                Text(
                    text = "BACK",
                    color = AnthropicSand,
                    fontSize = navFontSize,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            // END BUTTON (Closes all overlays / menus to live TV)
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.END) },
                shape = RoundedCornerShape(12.dp),
                backgroundColor = AnthropicSurfaceElevated,
                borderColor = AnthropicBorder,
                minSize = navBtnHeight,
                fontSize = navFontSize,
                modifier = Modifier
                    .weight(1f)
                    .height(navBtnHeight)
            ) {
                Text(
                    text = "END",
                    color = AnthropicSand,
                    fontSize = navFontSize,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            // HOME BUTTON
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.HOME) },
                shape = RoundedCornerShape(12.dp),
                backgroundColor = AnthropicSurfaceElevated,
                borderColor = AnthropicTerracotta.copy(alpha = 0.55f),
                minSize = navBtnHeight,
                fontSize = navFontSize,
                modifier = Modifier
                    .weight(1f)
                    .height(navBtnHeight)
            ) {
                Text(
                    text = "HOME",
                    color = AnthropicParchment,
                    fontSize = navFontSize,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

