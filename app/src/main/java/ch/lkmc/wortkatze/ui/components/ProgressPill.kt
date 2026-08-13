package ch.lkmc.wortkatze.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * The round's progress bar: a rounded track with a fill that animates to its
 * new value and carries a light cap on its leading edge, so the eye catches
 * the movement (docs/plan/07). [progress] is a fraction, 0f to 1f; anything
 * outside is clamped rather than trusted.
 *
 * The fill is driven by [animateFloatAsState] — it settles and stops, there
 * is no loop left running after the animation ends.
 */
@Composable
fun ProgressPill(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = Candy.Bubblegum,
    trackColor: Color = Candy.Lavender,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 300),
        label = "progressPillFill",
    )
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(CircleShape)
            .background(trackColor),
    ) {
        val fillWidth = maxWidth * animated
        if (animated > 0f) {
            Box(
                Modifier
                    .fillMaxHeight()
                    // Keep one pill-cap of fill visible at the very first step
                    // of a round, where the honest fraction would be a sliver.
                    .width(if (fillWidth < 14.dp) 14.dp else fillWidth)
                    .clip(CircleShape)
                    .background(fillColor),
            ) {
                Box(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Candy.Card.copy(alpha = 0.6f)),
                )
            }
        }
    }
}

@Preview(name = "Half", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun ProgressPillPreview() {
    WortkatzeTheme {
        ProgressPill(progress = 0.5f, modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "Empty and full", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun ProgressPillExtremesPreview() {
    WortkatzeTheme {
        androidx.compose.foundation.layout.Column(Modifier.padding(20.dp)) {
            ProgressPill(progress = 0f)
            ProgressPill(progress = 1f, modifier = Modifier.padding(top = 12.dp))
        }
    }
}
