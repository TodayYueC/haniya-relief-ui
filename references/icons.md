# Hand-drawn icons

Implementation: `compose/icon/ReliefIcons.kt`. Rendered samples: the tiles in `examples/components-*.svg`.

## Source paths
Each icon is a plain 24×24 SVG path string (Lucide-like geometry, round caps). Add new ones to the `paths` map; keep strokes open and simple — the engine adds the hand feel. ~60 general-purpose icons ship (navigation, actions, media, data, status, settings). Extend the table per project; the engine makes every new icon match.

## Sketch algorithm (per contour, seeded by icon name)
1. Measure the contour (Android `PathMeasure`), sample every 0.9 units.
2. Offset each sample along the normal by a low-frequency wobble: `amp · (0.65·sin(w1·s+p1) + 0.35·sin(w2·s+p2))`, `w1 = 2π/(6.5–9.5)`, `w2 = 2π/(2.6–3.8)`, amp ≈ 0.34 (scaled down for short contours).
3. Open contours: start ~0.2–0.5 early and **overshoot** the end by ~0.3–0.9 units along the tangent.
4. Closed contours: run 12% (0.8–2.2 units) past the start and drift outward (0.28·extra) so the loop visibly doesn't close.
5. Smooth with midpoint quadratic Béziers.
6. Main stroke: width 1.6, alpha 1. Second "graphite" stroke: different seed, amp 0.55, width 0.8, alpha 0.34, trimmed 8% at both ends.
7. Dots (< 1.2 units) become a tiny jittered tick.
8. Cache the resulting `ImageVector` per name → identical every frame.

## Coloring
- Default tint `ink`/`ink2` for neutral glyphs (chevrons @70%).
- Accent icons use the **gradient brush** (`HIcon(name, brush = c.brush)`), rendered via an offscreen layer + `SrcIn` rectangle.
- White on gradient surfaces (FAB, checked circle).

## Don'ts
No filled icons, no emoji as UI icons, no mixing with Material icons, no stroke width > 1.8 at 24 units, no animated wobble.
