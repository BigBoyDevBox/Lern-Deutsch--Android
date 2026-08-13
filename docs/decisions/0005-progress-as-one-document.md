# 0005 — Progress is one serialized document, not a database

- **Status:** accepted
- **Date:** 2026-08-13

## Context

The app has to remember per-card scheduling state for up to 1204 cards, plus
per-deck stars, a streak, XP, stickers, a daily goal and a handful of settings.

The obvious Android answer is Room: entities, a DAO, migrations, and a `Flow`
per query. The alternative is one serialized document in a typed DataStore.

Sizing it settles most of the argument. A `CardProgress` is five small numbers;
1204 of them plus their ids serialize to roughly 60–90 KB of JSON. DataStore
rewrites the whole file on every write, which at that size is a few
milliseconds.

## Decision

All persistent state is one `@Serializable data class Progress`, stored in a
`DataStore<Progress>` with a `kotlinx.serialization` serializer, in
`progress.json`.

Two rules make it safe:

- **Write once per finished round, not once per answer.** The ViewModel
  accumulates outcomes in memory and writes at the end of a round (and from
  `onStop`, so an abandoned round is not lost).
- **Corruption falls back to `Progress()`.** A `ReplaceFileCorruptionHandler`
  returns a fresh document rather than throwing, and an unknown `schema` is
  treated the same way.

## Consequences

- Every state change is a **pure function on an immutable document**, so the
  whole of `learn/Progress.kt` is unit-tested on the JVM with no Android
  runtime. That is the real win, and it is the same reason `learn/` is pure at
  all.
- Migrations are `kotlinx.serialization` defaults: a new field with a default
  value reads old documents unchanged. Anything more complicated than that is
  a signal to reconsider this decision, not to hand-write a migration.
- A learner who loses the file loses her streak and her boxes. Backup includes
  the DataStore directory precisely because of this.
- **The revisit trigger, stated so it can be checked:** move to Room if the
  document exceeds ~1 MB, or if writes have to become per-answer, or if any
  query needs to be indexed rather than filtered in memory. None of the three
  is close at 1204 cards.
