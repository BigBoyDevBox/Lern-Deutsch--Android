package ch.lkmc.wortkatze.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.lkmc.wortkatze.R
import ch.lkmc.wortkatze.ui.theme.Candy
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme

/**
 * One to three stars: gold when earned, a faint outline when not
 * (docs/plan/07). Earned and unearned differ in *shape* (filled vs outline)
 * as well as hue, so the count survives a colour-vision deficiency.
 *
 * The row announces itself as "2 of 3 stars"; the icons themselves are
 * decorative, because announcing "star star star" teaches nobody anything.
 */
@Composable
fun StarRow(
    earned: Int,
    modifier: Modifier = Modifier,
    total: Int = 3,
    starSize: Dp = 28.dp,
) {
    val clamped = earned.coerceIn(0, total)
    val description = stringResource(R.string.cd_stars, clamped, total)
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics { contentDescription = description },
    ) {
        repeat(total) { index ->
            if (index < clamped) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Candy.Gold,
                    modifier = Modifier.size(starSize),
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = Candy.InkSoft.copy(alpha = 0.45f),
                    modifier = Modifier.size(starSize),
                )
            }
        }
    }
}

@Preview(name = "2 of 3", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun StarRowPreview() {
    WortkatzeTheme {
        StarRow(earned = 2, modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "None and all", showBackground = true, backgroundColor = 0xFFFFF8F3)
@Composable
private fun StarRowExtremesPreview() {
    WortkatzeTheme {
        Row(Modifier.padding(20.dp)) {
            StarRow(earned = 0)
            StarRow(earned = 3, modifier = Modifier.padding(start = 16.dp))
        }
    }
}
