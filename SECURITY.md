# Security

## What this app does with your data

Nothing leaves the device. Wortkatze declares **no Android permissions at all**
and has no network code (`docs/decisions/0004`):

- The word list ships inside the APK.
- Learning progress is written to the app's own private files directory. It
  contains no name, no account and no contact details — only which German words
  have been practised.
- German pronunciation uses the phone's built-in text-to-speech engine, which
  runs locally.
- There is no analytics, no crash reporting, no advertising and no sync.

Progress is included in Android's own backup (`res/xml/backup_rules.xml`), so
it can follow you to a new phone. That transfer is Google's, governed by your
Android backup settings, and can be turned off there.

## Signing and provenance

Releases are signed with the **checked-in debug keystore** in
`app/debug.keystore` — a deliberate, documented decision
(`docs/decisions/0002`). The key is public, so **the signature proves nothing
about who built the APK.**

What it does prove: any APK from a given clone can upgrade any other, and
anyone can rebuild a byte-comparable APK themselves.

Trust therefore rests on where you downloaded it:

- Install only from this repository's **GitHub Releases** page.
- Each release carries a `.sha256` file. Check it before installing:
  `sha256sum -c wortkatze-vX.Y.Z.apk.sha256`.
- The release APK is built by `.github/workflows/release.yml` from the tagged
  commit, with a gate that refuses to publish if the tag and the committed
  `versionName` disagree.

Do not install a Wortkatze APK from anywhere else.

## Reporting a problem

Open an issue on this repository. There is no private disclosure channel and no
bug bounty — this is a personal learning app with no server, no accounts and no
user data to breach.

If you believe you have found something that should not be public, say so in
the issue without the details and it will be moved somewhere appropriate.
