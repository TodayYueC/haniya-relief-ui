# Material recipes

Three surfaces cover the whole UI. Implementation: `compose/component/Glass.kt`.

## Raised — `Modifier.glass(shape, level)`
1. dropShadow `hi`, radius R, offset (−d, −d)
2. dropShadow `shadow`, radius R, offset (+d, +d)
3. background: linear gradient `faceHi → glass → faceLo`, top-left → bottom-right
4. clip to shape
5. bevel: 1.2dp stroke on the outline, gradient `white@.85 → transparent → shadow@.16`

Non-neutral tint (rare): face = `tint+10% white → tint → tint+6% black`, outer dark shadow uses `tint@0.40` instead of gray.

## Inset — `Modifier.insetSurface(shape, depth)`
1. background `lerp(bg, shadow, .05)` (dark: `track`)
2. innerShadow shade, radius 1.6d, offset (+0.9d, +0.9d)
3. innerShadow `hi`, radius 1.6d, offset (−0.9d, −0.9d)
4. clip

Ring tracks (Canvas) fake the groove with three arcs: base `track`, a full arc stroked with `linearGradient(shadow@.45 → transparent → hi@.95)` across the bounds, and a thin darker center line (`shadow@.12`, 35% width).

## Gradient glaze — `Modifier.gradientSurface(shape, brush, glow)`
Highlight shadow + same-hue glow below, gradient fill, inner white sheen top-left, faint inner shade bottom-right. Use on ≤ 2 elements per screen region.

## Press feedback — `Modifier.pressable()`
Content scales to ≤ 0.985 (140ms in, 220ms out) and a diagonal overlay `shadow@.20 → transparent → hi@.35` fades in, clipped by the preceding `glass()` — the surface looks pushed in. Built-in 500ms double-tap guard.

## Engraved divider
Column of two 1dp lines: `shadow@.22` then `hi@.9` (dark: `.6` / `.35`).

## Porting

CSS (Platinum Mist card):
```css
.card{background:linear-gradient(135deg,#F3F5F8,#E6EAF1 50%,#E3E7EE);border-radius:26px;
  box-shadow:-7px -7px 18px #FFFFFFF2, 7px 7px 18px #A9B5C98C, inset 1px 1px 0 #FFFFFFD9;}
.inset{background:#E3E7EE;box-shadow:inset 3.6px 3.6px 6.4px #A9B5C99E, inset -3.6px -3.6px 6.4px #FFFFFF;}
.primary{background:linear-gradient(135deg,#6A86F2,#AA8CF0);border-radius:999px;color:#fff;
  box-shadow:-4px -4px 11px #FFFFFFE6, 3.5px 6px 14px #8487F16B, inset 2px 2px 6px #FFFFFF66;}
```
SwiftUI: stack `.shadow(color: .white, radius: 9, x: -7, y: -7)` and `.shadow(color: shade.opacity(0.55), radius: 9, x: 7, y: 7)` (SwiftUI radius ≈ half of blur). Flutter: `BoxDecoration(boxShadow: [BoxShadow(color: white, offset: Offset(-7,-7), blurRadius: 18), BoxShadow(color: shade.withOpacity(.55), offset: Offset(7,7), blurRadius: 18)])`.

### Dark-theme pitfall (banding)
On dark backgrounds the highlight shadow is only a few 8-bit levels lighter than `bg`. Blurred over a short distance, renderers (browsers, some GPUs) quantize it into visible concentric light rings — it looks like ghosting around every card. In dark themes use a **shorter highlight offset (≈ 0.5×) with a wider blur (≈ 1.6×)**, keep the inset highlight faint (≤ 0.25), set `color-interpolation-filters="sRGB"` for SVG filters, and drop the faint second "graphite" stroke on icons.
