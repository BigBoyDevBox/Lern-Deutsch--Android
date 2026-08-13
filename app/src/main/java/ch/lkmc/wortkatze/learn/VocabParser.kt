package ch.lkmc.wortkatze.learn

import kotlinx.serialization.json.Json

/**
 * Turns `vocab.json` into a [Vocabulary]. Pure: hand it a string, get a model
 * or an exception — no Context, no I/O, fully unit-testable against the real
 * asset (see `VocabAssetTest`, which reads the shipped file off the test
 * classpath rather than a fixture that could drift).
 */
object VocabParser {

    private val json = Json {
        // The generator writes exactly the fields below, but a future field
        // must not crash an older build mid-round.
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    class IncompatibleSchema(found: Int) : IllegalStateException(
        "vocab.json has schema $found, this build understands ${Vocabulary.SCHEMA}"
    )

    fun parse(text: String): Vocabulary {
        val vocabulary = json.decodeFromString(Vocabulary.serializer(), text)
        if (vocabulary.schema != Vocabulary.SCHEMA) throw IncompatibleSchema(vocabulary.schema)
        return vocabulary
    }
}
