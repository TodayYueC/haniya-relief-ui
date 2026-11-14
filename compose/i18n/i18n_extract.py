"""Relief UI i18n catalog extractor.

Scans Kotlin sources for source-language (Chinese) string literals — the translation keys — and
writes source.txt, one template per line; ${...} / $name interpolations become {0}, {1}, ...

Usage:
    python i18n_extract.py <project-root> <out-dir> [--skip-dir name ...]

Then diff <out-dir>/source.txt against assets/i18n/source.txt and append the new lines, in the same
order, to every language file. Keep a unit test that asserts all catalogs stay line-aligned and keep
identical placeholder sets.
"""
import glob
import os
import re
import sys

CJK = re.compile(r'[㐀-鿿]')


def literals(src):
    """Yield the template for each single-line "..." literal; skips raw strings and comments."""
    i, n = 0, len(src)
    while i < n:
        c = src[i]
        if src.startswith('//', i):
            j = src.find('\n', i)
            i = n if j < 0 else j
            continue
        if src.startswith('/*', i):
            j = src.find('*/', i + 2)
            i = n if j < 0 else j + 2
            continue
        if src.startswith('"""', i):
            j = src.find('"""', i + 3)
            i = n if j < 0 else j + 3
            continue
        if c == "'":
            j = i + 1
            while j < n and src[j] != "'":
                j += 2 if src[j] == '\\' else 1
            i = j + 1
            continue
        if c == '"':
            j, parts, k, ok = i + 1, [], 0, True
            while j < n and src[j] != '"':
                ch = src[j]
                if ch == '\n':
                    ok = False
                    break
                if ch == '\\':
                    esc = src[j + 1]
                    parts.append({'n': '\n', 't': '\t', '"': '"', '\\': '\\', '$': '$', "'": "'"}.get(esc, esc))
                    j += 2
                    continue
                if ch == '$' and j + 1 < n and src[j + 1] == '{':
                    depth, m = 1, j + 2
                    while m < n and depth:
                        if src[m] == '{':
                            depth += 1
                        elif src[m] == '}':
                            depth -= 1
                        elif src[m] == '"':
                            m += 1
                            while m < n and src[m] != '"':
                                m += 2 if src[m] == '\\' else 1
                        m += 1
                    parts.append('{%d}' % k)
                    k += 1
                    j = m
                    continue
                if ch == '$' and j + 1 < n and (src[j + 1].isalpha() or src[j + 1] == '_'):
                    m = j + 1
                    while m < n and (src[m].isalnum() or src[m] == '_'):
                        m += 1
                    parts.append('{%d}' % k)
                    k += 1
                    j = m
                    continue
                parts.append(ch)
                j += 1
            if ok:
                yield ''.join(parts)
            i = j + 1
            continue
        i += 1


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        sys.exit(1)
    root, out_dir = sys.argv[1], sys.argv[2]
    skip = set(sys.argv[sys.argv.index('--skip-dir') + 1:]) if '--skip-dir' in sys.argv else set()
    os.makedirs(out_dir, exist_ok=True)
    seen = {}
    for p in glob.glob(os.path.join(root, '**', 'src', 'main', '**', '*.kt'), recursive=True):
        rel = os.path.relpath(p, root)
        parts = set(rel.split(os.sep))
        if 'build' in parts or parts & skip:
            continue
        for t in literals(open(p, encoding='utf-8').read()):
            # Skip regexes and CSV rows; they are never rendered.
            if not CJK.search(t) or '(?:' in t or '\\d' in t or t.count(',') >= 3:
                continue
            seen.setdefault(t, rel)
    items = sorted(seen, key=lambda s: (seen[s].replace(os.sep, '/'), s))
    with open(os.path.join(out_dir, 'source.txt'), 'w', encoding='utf-8', newline='\n') as f:
        for t in items:
            f.write(t.replace('\\', '\\\\').replace('\n', '\\n') + '\n')
    print(len(items), 'templates')


if __name__ == '__main__':
    main()
