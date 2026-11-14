---
name: haniya-relief-ui
description: A general-purpose "soft relief" visual language for apps — realistic neumorphism (same-color raised surfaces with dual soft shadows, pressed-in inset wells), a two-stop accent gradient used sparingly, procedurally hand-drawn pen icons, calm CJK-friendly typography, five palettes incl. a dark one, and runtime multi-language UI. Use when designing, building or restyling any app (Android/Jetpack Compose first; CSS, SwiftUI, Flutter recipes included) that should follow this unified style, or when asked for "浮雕 / 新拟态 / 柔光浮雕 / 写实浮雕 / neumorphic / soft UI", hand-drawn line icons, or "keep our relief design language".
---

# Haniya Relief UI

A reusable design specification. Not tied to any one product: it defines how surfaces, color, icons, type, motion and language support look and behave, so every app built with it shares one visual language.

The feel: premium, quiet, tactile. Everything is carved from one ceramic-like material; depth comes from light, not borders or color blocks; color is a rare glaze, never decoration.

## How to apply

1. Read `references/principles.md` — the rules that make or break the look.
2. Take exact values from `references/tokens.md` (palettes, shadows, radii, spacing, type, motion).
3. Build every surface from the three recipes in `references/materials.md`.
4. Compose screens from `references/components.md`; for Jetpack Compose copy `compose/` (package `com.example.relief`, rename to yours).
5. Icons: follow `references/icons.md`; never mix in stock filled icon sets.
6. Multiple languages: `references/i18n.md`.
7. Before finishing, run `references/checklist.md` against real screenshots (light, dark, one long-word language).

Visual reference: `examples/` (generic mockups generated from the tokens).

## The ten rules

1. **One material.** Page background and card face are the same color. A raised element is defined only by two soft shadows — highlight from the upper-left, cool shade from the lower-right — plus a faint polished bevel.
2. **Raised = container or control; inset = holds a value or is selected.** Fields, progress tracks, segmented wells, selected chips and the selected nav item are pressed into the surface.
3. **Gradient is a rare glaze.** Only primary actions, the floating action button, checked states, progress fills and accent line icons use `accent → acc2`. Never fill cards, tiles or large areas with it.
4. **Icon tiles are ceramic.** Same-color raised rounded squares; the line icon carries the color, not the tile.
5. **No hard borders, no flat divider lines.** Separators are engraved (shade line + highlight line).
6. **Hand-drawn pen icons** — low wobble, overshooting ends, loops that don't quite close, a faint second stroke — deterministic, never animated.
7. **Quiet type.** System CJK font for Chinese/Japanese/Korean, Inter with tabular figures for numbers; no negative tracking on CJK.
8. **Short, calm motion.** 140–280 ms with one standard easing; no bounce, no ambient animation; honor reduced motion.
9. **Generous space.** 20dp gutters, 18–20dp gaps, 18dp card padding, 26dp card corners; leave room for shadows.
10. **Gentle tone.** No alarm red for ordinary data, no shaming copy; a danger color exists only for irreversible actions.

## Compose quick start

```
compose/theme/      Colors.kt (ReliefPalette + ReliefColors + gradient helpers), Theme.kt (ReliefTheme), Type.kt, Motion.kt
compose/component/  Glass.kt (glass / insetSurface / gradientSurface), Press.kt, Buttons, Controls, Layout,
                    BottomNav, LiquidSwitch, Charts, Sheet, Toast, Avatar, I18nText
compose/icon/       ReliefIcons.kt (hand-drawn engine + 24×24 path table)
compose/i18n/       I18n.kt (runtime translation), i18n_extract.py
```

Wrap the app in `ReliefTheme(ReliefPalette.PLATINUM) { … }`, build only from these primitives, and derive any new primitive from `glass()` / `insetSurface()` / `gradientSurface()` instead of inventing a new look. Requirements are listed in `README.md`.

## Other platforms

`references/materials.md` gives CSS `box-shadow`, SwiftUI `.shadow` and Flutter `BoxShadow` equivalents with the same numbers.
