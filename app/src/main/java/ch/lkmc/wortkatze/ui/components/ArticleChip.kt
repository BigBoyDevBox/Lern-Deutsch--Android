package ch.lkmc.wortkatze.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.R
import ch.lkmc.wortkatze.learn.Gender
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * der/die/das as a pill in its own colour — and always *with its word*
 * (docs/plan/07, docs/plan/20): colour is never the only signal, so the chip
 * carries the article text, which is also why the three article colours stay
 * reserved for gender and nothing else. The words themselves are content, not
 * UI, and are never translated.
 *
 * A [Gender.NONE] card renders nothing rather than a fourth, meaningless
 * colour: non-nouns simply have no chip.
 */
@Composable
fun ArticleChip(
    gender: Gender,
    modifier: Modifier = Modifier,
) {
    val (color, wordRes) = when (gender) {
        Gender.DER -> Candy.Der to R.string.article_der
        Gender.DIE -> Candy.Die to R.string.article_die
        Gender.DAS -> Candy.Das to R.string.article_das
        Gender.NONE -> return
    }
    Surface(
        shape = CircleShape,
        color = color,
        contentColor = Color.White,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(wordRes),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Preview(name = "der die das", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun ArticleChipPreview() {
    WortkatzeTheme {
        androidx.compose.foundation.layout.Row(Modifier.padding(20.dp)) {
            ArticleChip(Gender.DER)
            ArticleChip(Gender.DIE, Modifier.padding(start = 8.dp))
            ArticleChip(Gender.DAS, Modifier.padding(start = 8.dp))
        }
    }
}

@Preview(name = "200 % font", fontScale = 2f, showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun ArticleChipExtremePreview() {
    WortkatzeTheme {
        ArticleChip(Gender.DIE, Modifier.padding(20.dp))
    }
}
