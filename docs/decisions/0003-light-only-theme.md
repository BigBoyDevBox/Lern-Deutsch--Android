# 0003 — A light-only theme

- **Status:** accepted
- **Date:** 2026-08-13

## Context

Material 3 makes a dark theme cheap: define a second colour scheme, read
`isSystemInDarkTheme()`, done. Users expect it, and a phone app that ignores
the system setting looks unfinished.

But Wortkatze's whole visual identity is candy floss — cream ground, white
sticker cards, soft pink shadows, saturated pastels. Inverting those tokens
does not produce a dark candy app; it produces a muddy one. Pastels on black
lose their sweetness and gain a neon quality nobody asked for, and the soft
white-on-cream shadows that make the sticker metaphor work have no dark
equivalent at all.

The three article colours make it worse: der/die/das are a *learning aid*
inherited from the Pebble app, and their recognisability is load-bearing. A
dark theme that shifts them for contrast breaks the association; one that keeps
them fails contrast on a dark ground.

## Decision

Ship **light only**. `WortkatzeTheme` uses `lightColorScheme` unconditionally
and deliberately does not read `isSystemInDarkTheme()`. The window theme sets
`windowLightStatusBar=true`.

A dark theme, if it is ever built, is a *designed* palette — different ground,
different shadow strategy, re-checked article colours — not an inversion of
this one.

## Consequences

- The app is bright at night. For a learner who studies in bed this is a real
  cost, and the honest answer for now is the phone's own brightness slider.
- Every colour decision has exactly one context, which keeps the palette small
  and the contrast maths checkable ([20](../plan/20-accessibility.md)).
- Screens may not branch on the theme, so no screen accidentally becomes
  dark-only-correct.
- Revisit when: the learner asks for it, or evening use turns out to be the
  common case. The work is a design pass plus a contrast audit of the three
  article colours — not a token swap. It is listed as post-v1 in
  [23](../plan/23-roadmap-modes.md).
