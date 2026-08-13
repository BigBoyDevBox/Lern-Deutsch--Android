# 0004 — Offline, with no permissions

- **Status:** accepted
- **Date:** 2026-08-13

## Context

The app is for a 14-year-old. Anything it sends anywhere is a thing that has to
be explained, secured, and kept true for years. Anything it asks permission for
is a dialog she has to answer before she can learn a word.

Everything the app actually needs is local: the word list ships in the APK
(~250 KB of JSON), progress lives in the app's own files directory, and German
pronunciation comes from the on-device text-to-speech engine.

## Decision

`AndroidManifest.xml` declares **no permissions at all** — not INTERNET, not
storage, not notifications. There is no analytics, no crash reporting, no
account, no sync, no background work.

The one `<queries>` entry is package visibility, not a permission: without it
the app cannot see whether a TTS engine exists and so could not hide the
pronounce button on a device with none.

## Consequences

- There is nothing to write a privacy policy about, nothing to moderate, and
  no data to lose. This is worth more than any feature the network would buy.
- **No crash reporting.** Bugs arrive as "it did a weird thing" and are
  reproduced by hand. Accepted: the learning core is deterministic given a seed
  and a date, which is most of a repro.
- **No cloud sync.** Progress moves between phones only through Android's own
  backup, which is why both backup allowlists include the DataStore directory.
- **No notifications**, so no streak reminders. Deliberate — see
  [17](../plan/17-rewards.md): the streak is a nudge, not a debt.
- Any future feature needing the network (shared decks, a dictionary lookup,
  cloud sync) is a product decision requiring its own ADR, not an
  implementation detail.
