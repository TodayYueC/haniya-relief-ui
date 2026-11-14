# Components

All in `compose/component/`. Visual reference: `examples/`.

| Component | Recipe | Notes |
|---|---|---|
| `GlassCard` | raised Regular, r26, pad 18 | the only container; `onClick` uses pressable (sink, no scale) |
| `CardHeader` | `IconTile(36)` + title h3 + caption + trailing | home tiles, sections |
| `IconTile(icon, size, filled)` | raised Thin rounded square (r = .32·size), gradient line icon at .56·size | **never** gradient-filled |
| `IconChip` | bare gradient line icon | inline in text rows |
| `ListRow` | `IconTile(34, filled=false)` + title + value (≤156dp, 2 lines) + chevron @70% | settings lists; separate rows with engraved `Divider` |
| `PrimaryButton` | pill, gradient glaze, white text | one per view region |
| `GlassButton` | pill, raised Thin, ink text, icon tinted with gradient | secondary |
| `IconCircleButton` / `SmallRoundButton` | raised circle 44 / 36 | back, search, close |
| `HChip` | unselected raised pill → selected inset pill + `mid` text | filters, presets, options |
| `SegmentedControl` | inset pill well (46) + raised sliding thumb, selected text `mid` when `accentThumb` | ranges, effort levels |
| `LevelPicker` | 5 raised blocks, selected = gradient glaze + white text | 1–5 ratings |
| `CheckCircle` | unchecked inset hole → checked gradient circle with white hand-drawn check | to-dos, multi-select |
| `LiquidSwitch` | inset track, gradient fill fades in, raised pearl knob (28) | draggable, Role.Switch |
| `Stepper` | inset pill + raised round ± buttons with gradient icons | numeric input |
| `ProgressBar` | inset track (≥8 high) + gradient glaze fill, min visible 4% | goals, onboarding progress |
| `ProgressRing` | grooved track + glow arc + glaze arc + thin highlight; optional pearl knob | put large rings inside a raised disc (`glass(CircleShape)`) — the "dial" |
| `BarChart` / `LineChart` / `Sparkline` | grooved bar tracks, vertical gradient bars; line with soft area fill and pearl end dot | dashed light grid only |
| `GlassField` | inset r16, focus ring `mid@.55` 1.2dp | no underline, no floating label |
| `GlassSheet` | raised Thick r32, same color as page, scrim, drag handle as tiny inset bar | bottom sheets; tiles inside are raised Thin |
| `ToastHost` | raised Thick pill from top, optional "undo" in `soft` | |
| `GlassBottomNav` | raised Thick r28 bar, icon-only, selected = inset well + gradient icon; FAB 58 gradient circle | labels as contentDescription |
| `UserAvatar` | raised circle ring (7% padding) holding photo, or gradient disc with white initial | header 50, card 64, profile 104 + camera badge |
| `StatTile` | inset r16, label caption + Inter number | stats rows |
| `EmptyState` | `IconTile(56, filled=false)` + h3 + caption + GlassButton | |

## Screen patterns
- **Dashboard** (`examples/dashboard-*.svg`): header row (avatar · date/greeting · one or two round icon buttons) → large page title → full-width hero card (headline number + ring) → two-column metric cards with equal min height → list card with engraved dividers → floating icon-only nav + gradient FAB.
- **Timer / goal dial**: raised disc > grooved ring > centered Inter number + small accent caption; stage segments as short inset / glaze bars; an inset stats strip; one primary and one secondary pill below.
- **Settings**: tab title, profile card with avatar, section titles outside the cards, list cards with `IconTile` rows and engraved dividers, switches on the right.
- **Forms / onboarding**: progress bar on top, large title + caption, option cards raised → selected inset with a filled `CheckCircle`; one primary pill at the bottom.
- **Bottom sheets**: same color as the page, grid of raised Thin tiles with gradient line icons, close button as a small raised circle.
- **Components sheet** (`examples/components-*.svg`): primary / secondary pills, chips (raised vs. inset), segmented control, inset field, progress, check states, switches, level picker, icon tiles, bar chart.
