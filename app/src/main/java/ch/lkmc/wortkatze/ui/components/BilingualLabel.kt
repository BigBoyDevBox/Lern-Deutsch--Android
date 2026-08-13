package ch.lkmc.wortkatze.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * „Karten · 卡片" — the German word first, then the local one
 * (docs/plan/19). The German half is *content*: it is a word she is supposed
 * to learn, so it is never translated away. When the two strings are equal —
 * the German locale defines its half as the same word — the second half is
 * dropped and the label reads simply „Karten".
 *
 * The two halves are two independent labels joined by the `·` separator the
 * plan fixes for every locale, not a sentence built by concatenation.
 */
@Composable
fun BilingualLabel(
    german: String,
    local: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = Candy.Ink,
    localColor: Color = Candy.InkSoft,
) {
    if (german == local) {
        Text(text = german, style = style, color = color, modifier = modifier)
    } else {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = color)) { append(german) }
                withStyle(SpanStyle(color = localColor)) {
                    append(" · ")
                    append(local)
                }
            },
            style = style,
            color = color,
            modifier = modifier,
        )
    }
}

@Preview(name = "Chinese device", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun BilingualLabelPreview() {
    WortkatzeTheme {
        BilingualLabel(german = "Karten", local = "卡片", modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "German locale", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun BilingualLabelGermanPreview() {
    WortkatzeTheme {
        BilingualLabel(german = "Karten", local = "Karten", modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "Long label, 200 % font", fontScale = 2f, showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun BilingualLabelExtremePreview() {
    WortkatzeTheme {
        BilingualLabel(
            german = "Hausaufgabenbetreuungsstelle",
            local = "课外作业辅导班",
            modifier = Modifier.padding(20.dp),
        )
    }
}
