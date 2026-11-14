# Review checklist

Take real device screenshots of your app (light, dark, and one long-word language such as German) and check:

**Material**
- [ ] Page and cards are the same color; depth comes only from the two soft shadows + bevel.
- [ ] Light comes from the upper-left everywhere (shadows, grooves, press overlay).
- [ ] Every value holder (field, track, well, selected item) is inset; every container/control is raised.
- [ ] No hard borders or flat gray divider lines (engraved dividers only).
- [ ] Shadows aren't clipped by scroll containers or parents.

**Color**
- [ ] Gradient appears only on primary action, FAB, checks, progress, selected icon/line icons.
- [ ] Icon tiles are ceramic (same color), not gradient-filled.
- [ ] No red or alarm colors for normal data; danger only for irreversible actions.
- [ ] Dark theme uses lighter-gray highlight shadows, not white.

**Icons & type**
- [ ] All icons come from the hand-drawn set; none filled; none flicker on recomposition.
- [ ] Numbers use Inter tabular figures; Chinese has no negative tracking.
- [ ] Secondary text is readable (≥ 4:1).

**Layout & motion**
- [ ] 20dp gutters, 18–20dp gaps, r26 cards; side-by-side cards equal height.
- [ ] Motion ≤ 280ms (color 700ms), no bounce, reduced-motion snaps.
- [ ] Touch targets ≥ 44dp; icon-only buttons have content descriptions.

**Languages**
- [ ] Switching language updates instantly; dates/weekdays localized.
- [ ] No titles squeezed into per-letter wraps; long values ellipsize at 2 lines.
- [ ] User-written text and AI output are not run through translation.
