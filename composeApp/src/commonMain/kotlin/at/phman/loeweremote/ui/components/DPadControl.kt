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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.LoeweAccent
import at.phman.loeweremote.ui.theme.LoeweBorder
import at.phman.loeweremote.ui.theme.LoeweSurfaceDark
import at.phman.loeweremote.ui.theme.LoeweSurfaceElevated
import at.phman.loeweremote.ui.theme.LoeweTextMuted
import at.phman.loeweremote.ui.theme.LoeweTextPrimary
import at.phman.loeweremote.ui.theme.LoeweTextSecondary

@Composable
fun DPadControl(
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // D-Pad Outer Frame
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            LoeweSurfaceElevated,
                            LoeweSurfaceDark
                        )
                    )
                )
                .border(1.5.dp, LoeweBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // UP
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.UP) },
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    modifier = Modifier.size(width = 72.dp, height = 50.dp)
                ) {
                    Text("▲", color = LoeweTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // DOWN
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.DOWN) },
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp, topStart = 8.dp, topEnd = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    modifier = Modifier.size(width = 72.dp, height = 50.dp)
                ) {
                    Text("▼", color = LoeweTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // LEFT
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.LEFT) },
                    shape = RoundedCornerShape(topStart = 40.dp, bottomStart = 40.dp, topEnd = 8.dp, bottomEnd = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    modifier = Modifier.size(width = 50.dp, height = 72.dp)
                ) {
                    Text("◀", color = LoeweTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // RIGHT
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                RemoteButton(
                    onClick = { onKeyClick(LoeweKey.RIGHT) },
                    shape = RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp, topStart = 8.dp, bottomStart = 8.dp),
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Transparent,
                    modifier = Modifier.size(width = 50.dp, height = 72.dp)
                ) {
                    Text("▶", color = LoeweTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // CENTER OK BUTTON
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.OK) },
                shape = CircleShape,
                backgroundColor = LoeweSurfaceElevated,
                borderColor = LoeweAccent.copy(alpha = 0.6f),
                minSize = 74.dp,
                modifier = Modifier.size(74.dp)
            ) {
                Text(
                    text = "OK",
                    color = LoeweTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Back & Home Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK BUTTON
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.BACK) },
                shape = RoundedCornerShape(16.dp),
                minSize = 50.dp,
                modifier = Modifier.size(width = 100.dp, height = 46.dp)
            ) {
                Text(
                    text = "BACK",
                    color = LoeweTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // HOME BUTTON
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.HOME) },
                shape = RoundedCornerShape(16.dp),
                minSize = 50.dp,
                borderColor = LoeweAccent.copy(alpha = 0.4f),
                modifier = Modifier.size(width = 100.dp, height = 46.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("HOME", color = LoeweTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
