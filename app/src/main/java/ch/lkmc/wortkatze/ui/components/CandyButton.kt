package ch.lkmc.wortkatze.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * The primary action: a filled pill, 56dp tall, that springs to 0.94 while
 * pressed (docs/plan/07). The press is a scale + a shadow that drops from the
 * resting 4dp sticker elevation to 1dp, so the button reads as pushed into
 * the page.
 *
 * The animations are driven by press state, so they end with the press and
 * with the composable leaving composition — there is no loop to cancel.
 */
@Composable
fun CandyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Candy.Bubblegum,
    contentColor: Color = Candy.Card,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "candyButtonScale",
    )
    val elevation by animateDpAsState(
        targetValue = if (pressed) 1.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "candyButtonElevation",
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource,
        modifier = modifier
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                ambientColor = Candy.Shadow,
                spotColor = Candy.Shadow,
            ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 28.dp),
        )
    }
}

@Preview(name = "Default", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun CandyButtonPreview() {
    WortkatzeTheme {
        CandyButton(text = "Los geht's!", onClick = {}, modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "Long text, 200 % font", fontScale = 2f, showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun CandyButtonExtremePreview() {
    WortkatzeTheme {
        CandyButton(
            text = "Rindfleischetikettierungsüberwachungsaufgabenübertragungsgesetz",
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Disabled", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun CandyButtonDisabledPreview() {
    WortkatzeTheme {
        CandyButton(text = "Noch nicht", onClick = {}, enabled = false, modifier = Modifier.padding(20.dp))
    }
}
