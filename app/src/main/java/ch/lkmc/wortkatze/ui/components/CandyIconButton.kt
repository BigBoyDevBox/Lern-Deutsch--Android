package ch.lkmc.wortkatze.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * A round 48dp button carrying one emoji (docs/plan/07). Emoji rather than a
 * drawable: the app's art direction is emoji-first, so there is nothing to
 * tint, ship or keep in sync. Because the glyph itself is the only label,
 * [contentDescription] is what a screen reader gets — pass it whenever the
 * emoji is not self-explanatory.
 */
@Composable
fun CandyIconButton(
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "candyIconButtonScale",
    )
    // Bound once outside the semantics lambda, where the name would otherwise
    // shadow the parameter and assign the property to itself.
    val description = contentDescription

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Candy.Card,
        contentColor = Candy.Ink,
        interactionSource = interactionSource,
        modifier = modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (pressed) 1.dp else 4.dp,
                shape = CircleShape,
                ambientColor = Candy.Shadow,
                spotColor = Candy.Shadow,
            )
            .then(
                if (description != null) {
                    Modifier.semantics { this.contentDescription = description }
                } else {
                    Modifier
                },
            ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview(name = "Default", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun CandyIconButtonPreview() {
    WortkatzeTheme {
        CandyIconButton(emoji = "🔊", onClick = {}, modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "200 % font", fontScale = 2f, showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun CandyIconButtonExtremePreview() {
    WortkatzeTheme {
        CandyIconButton(emoji = "⚙️", onClick = {}, modifier = Modifier.padding(20.dp))
    }
}
