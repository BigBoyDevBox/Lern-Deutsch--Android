package ch.lkmc.wortkatze.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.R
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * The streak flame and its day count (docs/plan/07). The flame is an emoji —
 * decorative, like the deck icons — and the count next to it is plain text,
 * so nothing depends on reading the picture. The merged semantics announce
 * "7-day streak" rather than "fire, 7".
 */
@Composable
fun StreakFlame(
    days: Int,
    modifier: Modifier = Modifier,
) {
    val count = days.coerceAtLeast(0)
    val description = pluralStringResource(R.plurals.cd_streak_days, count, count)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = description
        },
    ) {
        Text(text = "🔥", style = MaterialTheme.typography.titleMedium)
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Candy.Flame,
        )
    }
}

@Preview(name = "Default", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun StreakFlamePreview() {
    WortkatzeTheme {
        StreakFlame(days = 7, modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "Long streak, 200 % font", fontScale = 2f, showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun StreakFlameExtremePreview() {
    WortkatzeTheme {
        StreakFlame(days = 365, modifier = Modifier.padding(20.dp))
    }
}
