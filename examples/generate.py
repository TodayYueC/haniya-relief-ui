"""Generate generic Relief UI example mockups (SVG) from the design tokens."""
import os, math

OUT = os.path.dirname(os.path.abspath(__file__))  # writes the SVGs next to this script
os.makedirs(OUT, exist_ok=True)

LIGHT = dict(bg='#E6EAF1', face_hi='#F1F3F7', face_lo='#E2E6EE', hi='#FFFFFF', hi_a=.95, sh='#A9B5C9', sh_a=.55,
             ink='#2F3648', ink2='#6F7890', acc='#6A86F2', acc2='#AA8CF0', track='#E1E5EC', in_hi_a=1.0, in_sh_a=.62, glow=.42)
DARK = dict(bg='#262A33', face_hi='#292D36', face_lo='#23272F', hi='#30353F', hi_a=.6, sh='#14171D', sh_a=.8,
            ink='#E8EBF3', ink2='#9AA2B6', acc='#7E95FF', acc2='#B79CFF', track='#22262E', in_hi_a=.25, in_sh_a=.9, glow=.4, dark=True)


def defs(c):
    return f'''<defs>
<linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="{c['acc']}"/><stop offset="1" stop-color="{c['acc2']}"/></linearGradient>
<linearGradient id="gh" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="{c['acc']}"/><stop offset="1" stop-color="{c['acc2']}"/></linearGradient>
<linearGradient id="face" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="{c['face_hi']}"/><stop offset=".5" stop-color="{c['bg']}"/><stop offset="1" stop-color="{c['face_lo']}"/></linearGradient>
<linearGradient id="bevel" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#fff" stop-opacity="{.85 if c is LIGHT else .07}"/><stop offset=".5" stop-color="#fff" stop-opacity="0"/><stop offset="1" stop-color="{c['sh']}" stop-opacity=".18"/></linearGradient>
{shadow_filter('r', c, 7, 9)}{shadow_filter('t', c, 3.5, 4.5)}{shadow_filter('k', c, 10, 13)}
<filter id="in" x="-10%" y="-10%" width="120%" height="120%" color-interpolation-filters="sRGB">
 <feFlood flood-color="{c['sh']}" flood-opacity="{c['in_sh_a']}"/><feComposite in2="SourceAlpha" operator="out"/><feOffset dx="3.2" dy="3.2"/><feGaussianBlur stdDeviation="3"/><feComposite in2="SourceAlpha" operator="in" result="s"/>
 <feFlood flood-color="{c['hi']}" flood-opacity="{c['in_hi_a']}"/><feComposite in2="SourceAlpha" operator="out"/><feOffset dx="-3.2" dy="-3.2"/><feGaussianBlur stdDeviation="3"/><feComposite in2="SourceAlpha" operator="in" result="l"/>
 <feMerge><feMergeNode in="SourceGraphic"/><feMergeNode in="s"/><feMergeNode in="l"/></feMerge></filter>
<filter id="glow" x="-40%" y="-40%" width="180%" height="200%" color-interpolation-filters="sRGB">
 <feOffset in="SourceAlpha" dx="4" dy="7"/><feGaussianBlur stdDeviation="8"/><feFlood flood-color="{c['acc']}" flood-opacity="{c['glow']}"/><feComposite operator="in" in2="SourceAlpha"/><feComposite operator="in"/>
 <feMerge><feMergeNode/><feMergeNode in="SourceGraphic"/></feMerge></filter>
</defs>'''


def shadow_filter(fid, c, d, b):
    # Dark theme: the highlight is only a few 8-bit levels above bg, so give it a shorter offset and a
    # wider blur; otherwise browsers quantize it into visible concentric rings.
    hd, hb = (d * .5, b * 1.6) if c.get('dark') else (d, b)
    return f'''<filter id="{fid}" x="-40%" y="-40%" width="180%" height="180%" color-interpolation-filters="sRGB">
 <feOffset in="SourceAlpha" dx="{-hd}" dy="{-hd}" result="o1"/><feGaussianBlur in="o1" stdDeviation="{hb}" result="b1"/><feFlood flood-color="{c['hi']}" flood-opacity="{c['hi_a']}"/><feComposite in2="b1" operator="in" result="l"/>
 <feOffset in="SourceAlpha" dx="{d}" dy="{d}" result="o2"/><feGaussianBlur in="o2" stdDeviation="{b}" result="b2"/><feFlood flood-color="{c['sh']}" flood-opacity="{c['sh_a']}"/><feComposite in2="b2" operator="in" result="d"/>
 <feMerge><feMergeNode in="l"/><feMergeNode in="d"/><feMergeNode in="SourceGraphic"/></feMerge></filter>
'''


def raised(x, y, w, h, r, f='r'):
    return (f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="url(#face)" filter="url(#{f})"/>'
            f'<rect x="{x+.6}" y="{y+.6}" width="{w-1.2}" height="{h-1.2}" rx="{r}" fill="none" stroke="url(#bevel)" stroke-width="1.2"/>')


def inset(x, y, w, h, r, c):
    return f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="{c["track"]}" filter="url(#in)"/>'


def glaze(x, y, w, h, r):
    return (f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="url(#g)" filter="url(#glow)"/>'
            f'<rect x="{x+1}" y="{y+1}" width="{w-2}" height="{h/2}" rx="{min(r, h/2)}" fill="#fff" opacity=".12"/>')


def text(x, y, s, size, c, color=None, weight=500, anchor='start'):
    return f'<text x="{x}" y="{y}" font-size="{size}" font-weight="{weight}" fill="{color or c["ink"]}" text-anchor="{anchor}">{s}</text>'


# Hand-drawn-looking icon strokes (pre-wobbled paths, 24x24).
ICONS = {
    'home': 'M3.4 10.7C6.2 8.3 9.2 5.5 12.1 3.2C15 5.8 18.2 8.3 20.7 10.9M4.7 10.2C4.4 13.7 4.9 17.3 4.6 20.5C9.6 20.7 14.6 20.4 19.6 20.6C19.8 17 19.4 13.8 19.7 10.3',
    'chart': 'M3.4 20.9Q11.8 20.4 20.7 21M7.2 17Q6.8 13.9 7.3 10.6M12.2 17.1Q11.8 11.8 12.3 6.6M17.1 17.2Q17.5 12.5 17 8.6',
    'bell': 'M6.2 16.2C6.4 13 5.6 9.4 7.9 7.2C10 5 14.4 5.1 16.3 7.5C18.2 9.9 17.7 13.1 17.9 16.1L19.6 18.2C14.6 18.4 9.5 18.1 4.5 18.3ZM10.1 21.1Q12 21.4 13.9 21',
    'user': 'M12.1 3.6C9.9 3.4 8.1 5.2 8.2 7.6C8.1 10 9.8 11.8 12 11.7C14.4 11.8 16 10 15.9 7.6C16 5.2 14.3 3.5 12.1 3.6M4.6 20.7C4.7 16.4 7.8 13.9 12 14C16.1 13.8 19.3 16.5 19.5 20.6',
    'spark': 'M12.1 3.4Q12.9 6.3 14 8.3Q16.2 9.1 18.8 10.1Q16.2 11 13.9 11.9Q12.9 14.4 12 16.8Q11.1 14.1 10.2 11.9Q7.7 10.8 5.2 10Q7.8 9.1 10.2 8.2Q11.3 5.9 12.1 3.4M19.2 3.4L19 6.9M17.5 5.1L20.8 5.2',
    'drop': 'M12 3.2C10.2 5.5 6 10.2 6.1 14C6 17.7 8.4 20.8 12.1 20.6C15.8 20.8 18.1 17.7 17.9 14C18 10.1 13.9 5.3 12 3.2M8.7 14.8Q8.9 17.1 10.8 17.9',
    'moon': 'M19.8 14.8C15.9 16.4 10.9 14 9.3 10.4C8.3 8.1 8.4 6 9.3 4C5.7 5.3 3.8 8.1 4 11.9C3.8 16.8 7.7 20.3 12.4 20.2C15.9 20.3 18.9 18 19.8 14.8',
    'plus': 'M12.1 5.1Q11.8 11.9 12.1 18.8M5.1 12.1Q12 11.8 18.9 12',
    'check': 'M5.2 12.6Q7.4 14.5 9.5 16.8Q14.2 11.9 18.9 7.2',
    'search': 'M10.6 4.1C6.9 4 4 7.1 4.2 10.8C4.1 14.6 7 17.4 10.8 17.2C14.6 17.4 17.3 14.3 17.2 10.6C17.3 6.9 14.4 4 10.6 4.1M15.5 15.7Q17.9 18 20.2 20.1',
    'chevR': 'M9.1 6.2Q11.9 8.9 14.8 12.1Q12 15.2 9.2 17.8',
    'leaf': 'M5 19C5.2 11.2 10.4 5.6 19.2 4.8C19.6 13.4 13.6 19.4 5 19ZM5.2 18.8Q10.2 13.6 14.6 9.6',
}


DARK_MODE = [False]


def icon(name, x, y, size, stroke, ghost=None):
    """Hand-drawn icon; the faint graphite echo is skipped on dark backgrounds (it reads as a ghost)."""
    if ghost is None:
        ghost = 0 if DARK_MODE[0] else .34
    sc = size / 24
    d = ICONS[name]
    out = (f'<g transform="translate({x},{y}) scale({sc})" fill="none" stroke-linecap="round" stroke-linejoin="round">'
           f'<path d="{d}" stroke="{stroke}" stroke-width="1.6"/>')
    if ghost:
        out += f'<path d="{d}" stroke="{stroke}" stroke-width=".8" opacity="{ghost}" transform="translate(.35,.25)"/>'
    return out + '</g>'


def tile(name, x, y, size, c=None):
    return raised(x, y, size, size, size * .32, 't') + icon(name, x + size * .22, y + size * .22, size * .56, 'url(#g)')


def ring(cx, cy, r, sw, p, c):
    circ = 2 * math.pi * r
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="none" stroke="{c["track"]}" stroke-width="{sw}"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="none" stroke="{c["sh"]}" stroke-opacity=".25" stroke-width="{sw*.35}"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="none" stroke="url(#g)" stroke-width="{sw*.78}" stroke-linecap="round" '
            f'stroke-dasharray="{circ*p} {circ}" transform="rotate(-90 {cx} {cy})"/>')


def dashboard(c, name):
    DARK_MODE[0] = bool(c.get('dark'))
    W, H = 390, 844
    o = [f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {W} {H}" width="{W}" height="{H}" font-family="Inter, -apple-system, Segoe UI, Roboto, sans-serif">', defs(c),
         f'<rect width="{W}" height="{H}" fill="{c["bg"]}"/>']
    # header
    o.append(raised(20, 54, 48, 48, 24, 't'))
    o.append(f'<circle cx="44" cy="78" r="20" fill="url(#g)"/>' + text(44, 85, 'A', 18, c, '#fff', 600, 'middle'))
    o.append(text(82, 72, 'Saturday, 14 November', 12, c, c['ink2'], 400))
    o.append(text(82, 94, 'Good morning, Alex', 19, c, None, 600))
    o.append(raised(326, 58, 44, 44, 22, 't') + icon('bell', 336, 68, 24, c['ink']))
    o.append(text(20, 150, 'Today', 28, c, None, 650))
    # hero card with dial
    o.append(raised(20, 176, 350, 168, 26))
    o.append(tile('leaf', 40, 196, 36, c) + text(88, 220, 'Daily focus', 15, c))
    o.append(text(350, 220, 'On track', 13, c, c['acc'], 600, 'end'))
    o.append(text(40, 286, '4h 32m', 34, c, None, 600))
    o.append(text(40, 312, 'Goal 6h · ends ~18:40', 12, c, c['ink2'], 400))
    o.append(ring(304, 280, 34, 10, .72, c) + text(304, 286, '72%', 15, c, None, 600, 'middle'))
    # metric tiles
    for i, (ic, title, val, unit, sub, prog) in enumerate([('drop', 'Water', '1.4', 'L', 'Goal 2.0 L', .7), ('moon', 'Sleep', '7h 20', 'm', 'Quality · Good', None)]):
        x = 20 + i * 184
        o.append(raised(x, 368, 166, 168, 26))
        o.append(tile(ic, x + 18, 386, 36, c) + text(x + 64, 410, title, 15, c))
        o.append(f'<text x="{x+18}" y="474" font-size="30" font-weight="600" fill="{c["ink"]}">{val}<tspan font-size="13" font-weight="400" fill="{c["ink2"]}" dx="4">{unit}</tspan></text>')
        o.append(text(x + 18, 498, sub, 12, c, c['ink2'], 400))
        if prog:
            o.append(inset(x + 18, 512, 130, 10, 5, c) + f'<rect x="{x+20}" y="514" width="{126*prog}" height="6" rx="3" fill="url(#gh)"/>')
    # list card
    o.append(raised(20, 560, 350, 150, 26))
    rows = [('spark', 'Smart reminders', 'switch'), ('chart', 'Weekly report', 'Monday')]
    for j, (ic, label, kind) in enumerate(rows):
        y = 580 + j * 64
        o.append(tile(ic, 40, y, 34, c) + text(88, y + 22, label, 15, c))
        if kind == 'switch':
            o.append(inset(298, y + 2, 52, 30, 15, c) + f'<rect x="298" y="{y+2}" width="52" height="30" rx="15" fill="url(#gh)"/>'
                     f'<circle cx="335" cy="{y+17}" r="12.5" fill="{c["face_hi"]}" filter="url(#t)"/>')
        else:
            o.append(text(326, y + 22, kind, 14, c, c['ink2'], 400, 'end') + icon('chevR', 330, y + 9, 18, c['ink2']))
        if j == 0:
            o.append(f'<rect x="40" y="{y+48}" width="310" height="1" fill="{c["sh"]}" opacity=".22"/><rect x="40" y="{y+49}" width="310" height="1" fill="{c["hi"]}" opacity=".9"/>')
    # bottom nav
    o.append(raised(16, 738, 276, 62, 28, 'k'))
    o.append(inset(22, 744, 52, 50, 22, c))
    for k, ic in enumerate(['home', 'chart', 'spark', 'user']):
        cx = 48 + k * 66
        o.append(icon(ic, cx - 12, 757, 24, 'url(#g)' if k == 0 else c['ink2']))
    o.append(f'<circle cx="335" cy="769" r="29" fill="url(#g)" filter="url(#glow)"/>' + icon('plus', 322, 756, 26, '#fff'))
    o.append('</svg>')
    open(os.path.join(OUT, name), 'w', encoding='utf-8', newline='\n').write('\n'.join(o))


def components(c, name):
    DARK_MODE[0] = bool(c.get('dark'))
    W, H = 390, 640
    o = [f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {W} {H}" width="{W}" height="{H}" font-family="Inter, -apple-system, Segoe UI, Roboto, sans-serif">', defs(c),
         f'<rect width="{W}" height="{H}" fill="{c["bg"]}"/>']
    o.append(text(20, 44, 'Components', 24, c, None, 650))
    # buttons
    o.append(glaze(20, 66, 168, 48, 24) + text(104, 96, 'Continue', 15, c, '#fff', 600, 'middle'))
    o.append(raised(204, 66, 166, 48, 24, 't') + text(287, 96, 'Later', 15, c, None, 500, 'middle'))
    # chips
    o.append(raised(20, 136, 96, 36, 18, 't') + text(68, 159, 'Daily', 13, c, None, 500, 'middle'))
    o.append(inset(128, 136, 104, 36, 18, c) + text(180, 159, 'Weekly', 13, c, c['acc'], 600, 'middle'))
    o.append(raised(244, 136, 112, 36, 18, 't') + text(300, 159, 'Monthly', 13, c, None, 500, 'middle'))
    # segmented
    o.append(inset(20, 194, 350, 46, 23, c) + raised(24, 198, 114, 38, 19, 't'))
    for i, s in enumerate(['7 days', '30 days', 'All']):
        o.append(text(81 + i * 116, 222, s, 13, c, c['acc'] if i == 0 else c['ink2'], 600 if i == 0 else 500, 'middle'))
    # field
    o.append(inset(20, 262, 350, 48, 16, c) + text(36, 291, 'Search notes…', 14, c, c['ink2'], 400))
    o.append(icon('search', 336, 274, 22, c['ink2']))
    # progress + stepper
    o.append(inset(20, 334, 230, 12, 6, c) + f'<rect x="22" y="336" width="150" height="8" rx="4" fill="url(#gh)"/>')
    o.append(text(262, 345, '65%', 13, c, None, 600))
    # checks + switches
    o.append(inset(20, 368, 28, 28, 14, c) + f'<circle cx="80" cy="382" r="14" fill="url(#g)" filter="url(#glow)"/>' + icon('check', 71, 373, 18, '#fff'))
    o.append(inset(120, 366, 52, 32, 16, c) + f'<circle cx="136" cy="382" r="13" fill="{c["face_hi"]}" filter="url(#t)"/>')
    o.append(f'<rect x="188" y="366" width="52" height="32" rx="16" fill="url(#gh)"/><circle cx="224" cy="382" r="13" fill="{c["face_hi"]}" filter="url(#t)"/>')
    # level picker
    for i, l in enumerate(['1', '2', '3', '4', '5']):
        x = 20 + i * 72
        if i == 2:
            o.append(glaze(x, 420, 62, 42, 12) + text(x + 31, 446, l, 14, c, '#fff', 600, 'middle'))
        else:
            o.append(raised(x, 420, 62, 42, 12, 't') + text(x + 31, 446, l, 14, c, None, 500, 'middle'))
    # icon tiles
    for i, ic in enumerate(['home', 'drop', 'moon', 'chart', 'spark', 'leaf']):
        o.append(tile(ic, 20 + i * 60, 490, 44, c))
    # bars
    vals = [.35, .6, .45, .8, .55, .9, .4]
    for i, v in enumerate(vals):
        x = 20 + i * 50
        o.append(f'<rect x="{x}" y="560" width="36" height="60" rx="8" fill="{c["track"]}" filter="url(#in)"/>')
        o.append(f'<rect x="{x}" y="{560 + 60*(1-v)}" width="36" height="{60*v}" rx="8" fill="url(#g)" opacity="{1 if i == 5 else .45}"/>')
    o.append('</svg>')
    open(os.path.join(OUT, name), 'w', encoding='utf-8', newline='\n').write('\n'.join(o))


dashboard(LIGHT, 'dashboard-light.svg')
dashboard(DARK, 'dashboard-dark.svg')
components(LIGHT, 'components-light.svg')
components(DARK, 'components-dark.svg')
print(os.listdir(OUT))
