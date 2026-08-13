package ch.lkmc.wortkatze.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.lkmc.wortkatze.R
import ch.lkmc.wortkatze.learn.Deck
import ch.lkmc.wortkatze.learn.Tier
import ch.lkmc.wortkatze.ui.theme.Candy

/**
 * The home screen — for now a plain shelf of every tier and deck, which is
 * enough to prove the vocabulary pipeline end to end on a real device.
 *
 * docs/plan/09-home-screen.md describes what this becomes: the mascot banner,
 * the streak flame, the daily goal ring and the five mode buttons. Replacing
 * this file is roadmap step 4; nothing else depends on its internals.
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(color = Candy.Cream, modifier = Modifier.fillMaxSize()) {
        when {
            state.loading -> Centered { CircularProgressIndicator(color = Candy.Bubblegum) }

            state.failed || state.vocabulary == null -> Centered {
                Text(
                    text = stringResource(R.string.vocab_load_failed),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp),
                )
            }

            else -> {
                val vocabulary = state.vocabulary!!
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Column(Modifier.padding(bottom = 8.dp)) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = Candy.Bubblegum,
                                )
                                Text(
                                    text = stringResource(R.string.app_name_zh),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Candy.Der,
                                    modifier = Modifier.padding(bottom = 6.dp),
                                )
                            }
                            Text(
                                text = stringResource(R.string.app_tagline),
                                style = MaterialTheme.typography.titleMedium,
                                color = Candy.InkSoft,
                            )
                        }
                    }
                    vocabulary.tiers.forEach { tier ->
                        item(key = "tier-${tier.id}") { TierHeading(tier) }
                        items(
                            items = vocabulary.decksIn(tier.id),
                            key = { "deck-${it.key}" },
                        ) { deck -> DeckRow(deck) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TierHeading(tier: Tier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(top = 16.dp),
    ) {
        Box(
            Modifier
                .size(14.dp)
                .background(Candy.accent(tier.accent), CircleShape)
        )
        Text(
            text = "${tier.de} · ${tier.zh}",
            style = MaterialTheme.typography.titleLarge,
            color = Candy.Ink,
        )
    }
}

@Composable
private fun DeckRow(deck: Deck) {
    Surface(
        color = Candy.Card,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(Candy.accent(deck.accent).copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(deck.icon, style = MaterialTheme.typography.titleLarge)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = deck.de,
                    style = MaterialTheme.typography.titleMedium,
                    color = Candy.Ink,
                )
                Text(
                    text = "${deck.zh} · ${deck.en}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Candy.InkSoft,
                )
            }
            Text(
                text = deck.cards.size.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Candy.accent(deck.accent),
            )
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}
