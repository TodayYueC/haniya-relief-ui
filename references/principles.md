# Principles

These are the decisions that define the style. Each was validated on real devices against a neumorphic reference; follow them unless a project explicitly overrides one.

## 1. A single ceramic material
- Background `bg` and card face `glass` are the **same color**. The relief only works if they match; never put white cards on a gray page.
- The card face carries an almost invisible diagonal gradient (`faceHi → glass → faceLo`) plus a 1.2dp "polished bevel" stroke (white top-left fading to a faint shade bottom-right). This is what makes it read *realistic* rather than flat.
- Light source is fixed: **upper-left**. Every shadow, bevel and groove obeys it.

## 2. Raised vs. inset has meaning
| Raised (`glass`) | Inset (`insetSurface`) |
|---|---|
| Cards, buttons, icon tiles, nav bar, sheets, avatar ring, knobs | Text fields, progress/ring tracks, segmented wells, selected chip, selected nav item, stat tiles, empty slots |
Pressing a raised element visually sinks it (dark top-left wash) — that is the tap feedback; no ripples.

## 3. Color is a glaze, used sparingly
- Accent is always a **two-stop gradient** `accent → acc2` (e.g. periwinkle → lavender).
- Allowed: primary button, floating "+", checked check circle, progress & ring fills, an active day/state marker, selected nav icon, line icons (as gradient tint).
- Forbidden: gradient-filled icon tiles, colored cards, colored section headers, colored backgrounds. Gradient-filled tiles were tried and rejected as cheap-looking — tiles stay ceramic.
- Gradient elements get a soft **same-hue glow** below them, never a gray drop shadow.

## 4. Hand-drawn line icons
Pen-sketch icons give warmth against the precise material. Wobble is low-frequency and small; ends overshoot; closed shapes don't quite meet; a faint second stroke adds graphite texture. Deterministic seed → identical every frame.

## 5. Calm typography
- Chinese: system font (Noto Sans CJK / Source Han). Letter-spacing 0–0.01em. Never negative.
- Numbers: Inter with `tnum`, slight negative tracking (−0.02em) only for digits.
- Hierarchy through size and weight (Medium/SemiBold), not color.
- Secondary text `ink2` is a desaturated slate, ≥ 4:1 on `bg`.

## 6. Motion
- Standard easing `cubic-bezier(0.2, 0.8, 0.2, 1)`; durations 140 (press) / 220 (move) / 280 (sheet, ring) / 700 (theme color).
- No springs that overshoot, no ambient animation, no staggered card entrances.
- Reduced motion → snap.

## 7. Layout rhythm
- Screen gutter 20dp; card spacing 18dp horizontal / 20dp vertical; card padding 18dp; corner 26dp.
- Shadows need room: never clip a raised element's parent tighter than ~26dp around it (use padding inside horizontal scrollers).
- Side-by-side cards share a min height so the grid looks carved, not stacked.

## 8. Tone
Neutral, encouraging copy. No alarm red for ordinary data, no "failed / exceeded" framing, no rankings or streak-shaming. The danger color is reserved for irreversible actions (delete, disconnect).

## 9. Language-proof layouts
- Row values capped (~156dp, 2 lines, end-aligned) so long German/Russian strings never squeeze titles into letter-wraps.
- Icon-only bottom navigation (labels kept as accessibility names) — it stays clean in every language.
- Prefer short native terms over long compounds in translations when space is tight.
