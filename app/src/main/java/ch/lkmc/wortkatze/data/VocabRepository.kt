package ch.lkmc.wortkatze.data

import android.content.Context
import ch.lkmc.wortkatze.learn.VocabParser
import ch.lkmc.wortkatze.learn.Vocabulary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads `assets/vocab.json` once and hands the parsed [Vocabulary] to whoever
 * asks. ~250 KB of JSON is a few tens of milliseconds to parse — enough to
 * drop frames on a slow phone, so it happens on [Dispatchers.IO] and the
 * result is kept for the process lifetime.
 *
 * The mutex means a screen rotation during the first load produces one parse,
 * not two; the double-check inside it means the common case never suspends on
 * a lock at all.
 */
@Singleton
class VocabRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mutex = Mutex()

    @Volatile
    private var cached: Vocabulary? = null

    suspend fun vocabulary(): Vocabulary {
        cached?.let { return it }
        return mutex.withLock {
            cached ?: load().also { cached = it }
        }
    }

    private suspend fun load(): Vocabulary = withContext(Dispatchers.IO) {
        val text = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        VocabParser.parse(text)
    }

    private companion object {
        const val ASSET = "vocab.json"
    }
}
