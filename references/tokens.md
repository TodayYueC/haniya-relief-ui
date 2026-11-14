# Tokens

All values in dp/sp. Hex is ARGB-less RGB unless noted.

## Palettes

Light palettes are generated from 7 inputs by `lightPalette()` (see `compose/theme/Colors.kt`):

| Theme (id) | bg | shadow | ink | ink2 | accent | acc2 |
|---|---|---|---|---|---|---|
| 铂雾 Platinum Mist (`PLATINUM`, default) | `#E6EAF1` | `#A9B5C9` | `#2F3648` | `#6F7890` | `#6A86F2` | `#AA8CF0` |
| 月岩 Moonstone (`MOONSTONE`) | `#E9E7EF` | `#B2ADC6` | `#332F45` | `#746E8C` | `#8072E0` | `#D291C8` |
| 香槟 Champagne (`CHAMPAGNE`) | `#EFEBE5` | `#C4B8A8` | `#3B342D` | `#7D7366` | `#BA8B5C` | `#E2A88C` |
| 烟玫 Smoky Rose (`ROSE`) | `#EFE8EB` | `#C6B3BC` | `#3C2F37` | `#80707A` | `#C77391` | `#E6A59A` |

Derived (light): `glass = bg`, `glass2 = lerp(bg, white, .10)`, `hi = white`, `line = lerp(bg, shadow, .32)`, `track = lerp(bg, shadow, .10)`, `soft = lerp(bg, accent, .12)`, `soft2 = lerp(bg, acc2, .14)`, `scrim = ink·0.3 black @ 32%`, `on = white`.
Computed helpers: `brush = linearGradient(accent, acc2)` (diagonal), `hBrush` (horizontal), `mid = lerp(accent, acc2, .35)` for single-color accents (selected text), `faceHi = lerp(glass, white, .42)`, `faceLo = lerp(glass, shadow, .05)`.

### Dark — 曜石 Obsidian (`OBSIDIAN`)
`bg/glass #262A33`, `glass2 #2B303A`, `hi #353B47` (light shadow is a *lighter gray*, not white), `shadow #14171D`, `ink #E8EBF3`, `ink2 #9AA2B6`, `accent #7E95FF`, `acc2 #B79CFF`, `track #22262E`, `line #363C48`, `soft #323B57`, `soft2 #3A3452`, `scrim #990C0E12`.

Fixed: amber `#D2A26C` (notices / needs-review), danger `#B4566A` light / `#E6A3AE` dark (irreversible actions only).

## Relief levels

| Level | Use | shadow radius | offset (±x, ±y) | light alpha | dark alpha |
|---|---|---|---|---|---|
| Thin | buttons, chips, icon tiles, knobs | 9 | 3.5 | 0.95 | 0.55 |
| Regular | cards, avatar | 18 | 7 | 0.95 | 0.55 |
| Thick | bottom nav, sheets, toasts | 26 | 10 | 0.95 | 0.55 |
Dark theme: light shadow alpha 0.55, dark shadow alpha 0.85.
Bevel stroke 1.2dp: white @0.85 → transparent → shadow @0.16 (dark theme: white @0.07 / black @0.25).

## Inset
`depth` default 4 (tracks 2–3, fields 3.5). Two inner shadows, radius `1.6·depth`, offset `±0.9·depth`: shade @0.62 from top-left, white @1.0 from bottom-right (dark: 0.95 / 0.55). Fill `lerp(bg, shadow, .05)`.

## Gradient surface
Fill `brush`; outer: highlight `hi@0.9` radius `0.8·glow` offset `−glow/3`; glow `mid@0.42` radius `glow` offset `(glow/4, glow/2.4)`; inner: white@0.40 r6 (2,2) and black@0.10 r6 (−2,−2). Typical glow: large button 14, small 10, FAB 14, check 6, chip 8.

## Radii
xs 12 · sm 16 · md 22 · card 26 · lg 28 · sheet 32 · pill 999. Buttons, chips, segmented, switches are pills. Icon tile corner = 0.32 × size.

## Spacing
Gutter 20 · card padding 18 · grid gap 18 h / 20 v · section title 22 top / 12 bottom · list row min height 52 · touch target ≥ 44.

## Sizes
Buttons 48 / 40 · chip 36 · segmented 46 · switch 52×32 (knob 28) · icon tile 34–44 · icon in tile 0.56× · bottom nav 62 high, FAB 58 · avatar 50 (header) / 64 (card) / 104 (profile) · progress dial: 236 raised disc holding a 204 ring, stroke 14.

## Typography
| Style | Size/Line | Weight |
|---|---|---|
| display | 28/38 | SemiBold |
| greeting | 24/34 | Medium |
| pageTitle | 20/28 | SemiBold |
| title | 17/25 | Medium |
| h3 | 15/23 | Medium |
| body | 14/23 | Normal |
| bodyMedium | 15/23 | Medium |
| label | 13/20 | Medium |
| caption / badge / tab | 12/18 | Normal / Medium |
| button / small | 15 / 14 | Medium |
Letter-spacing 0.01em for CJK. Numbers: Inter, `tnum`, −0.02em, sizes 52/44/34/28/22/19/14/12, Medium.

## Motion
Easing `CubicBezier(0.2, 0.8, 0.2, 1)`. press 140 · spring/move 220 · soft (sheet, ring, expand) 280 · color 700 · switch 180–200 · nav well 200. Press scale ≤ 0.985 plus sink overlay.
