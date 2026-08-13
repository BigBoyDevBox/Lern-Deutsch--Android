package ch.lkmc.wortkatze.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * The white rounded surface everything sits on — a sticker on a page, not a
 * Material sheet: a soft shadow in the one shadow colour, never tonal
 * elevation (which tints the surface and muddies the palette, docs/plan/07).
 *
 * [onClick] keeps the card inert by default; pass it to make the whole card
 * the touch target rather than nesting a clickable inside it.
 */
@Composable
fun StickerCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    elevation: Dp = 4.dp,
    bordered: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shadowed = modifier.shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Candy.Shadow,
        spotColor = Candy.Shadow,
    )
    val border = if (bordered) {
        androidx.compose.foundation.BorderStroke(2.dp, Candy.Card)
    } else {
        null
    }
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = shadowed,
            shape = shape,
            color = Candy.Card,
            contentColor = Candy.Ink,
            border = border,
            content = content,
        )
    } else {
        Surface(
            modifier = shadowed,
            shape = shape,
            color = Candy.Card,
            contentColor = Candy.Ink,
            border = border,
            content = content,
        )
    }
}

@Preview(name = "Default", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun StickerCardPreview() {
    WortkatzeTheme {
        StickerCard(Modifier.padding(20.dp)) {
            Text("Ein Kärtchen", Modifier.padding(16.dp))
        }
    }
}

@Preview(name = "Long text, 200 % font", fontScale = 2f, showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun StickerCardExtremePreview() {
    WortkatzeTheme {
        StickerCard(Modifier.padding(20.dp)) {
            Text(
                "Rindfleischetikettierungsüberwachungsaufgaben",
                Modifier.padding(16.dp),
            )
        }
    }
}
