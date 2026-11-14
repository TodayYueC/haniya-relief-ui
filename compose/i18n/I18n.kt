package com.example.relief.i18n

import android.content.Context
import android.content.res.Resources
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * 运行时界面翻译。
 *
 * 源码里的中文文案即为键：`assets/i18n/source.txt` 每行一条（插值写作 `{0}`、`{1}`），
 * 各语言文件逐行对应。渲染时按「完整匹配 → 模板匹配 → 按分隔符拆段」依次查找，
 * 找不到就原样显示中文，因此业务逻辑、存储和 AI 提示词都不受语言影响。
 */
object I18n {
    data class Language(val code: String, val nativeName: String)

    const val SOURCE = "zh-Hans"
    const val SYSTEM = "system"

    val languages = listOf(
        Language(SYSTEM, "跟随系统"),
        Language("zh-Hans", "简体中文"),
        Language("zh-Hant", "繁體中文"),
        Language("en", "English"),
        Language("ja", "日本語"),
        Language("ko", "한국어"),
        Language("es", "Español"),
        Language("fr", "Français"),
        Language("de", "Deutsch"),
        Language("pt", "Português"),
        Language("ru", "Русский"),
    )

    /** 当前生效的语言（已把「跟随系统」解析成具体语言）。 */
    @Volatile
    var lang: String = SOURCE
        private set

    private var appContext: Context? = null
    private val catalogs = ConcurrentHashMap<String, Catalog>()
    private const val PREFS = "i18n"

    fun init(context: Context) {
        appContext = context.applicationContext
        lang = resolve(choice(context))
        Locale.setDefault(locale(lang))
    }

    /** 用户的选择（可能是 [SYSTEM]）。同步保存在 SharedPreferences，供冷启动、通知与广播使用。 */
    fun choice(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("lang", SYSTEM) ?: SYSTEM

    fun setChoice(context: Context, code: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("lang", code).apply()
        lang = resolve(code)
        Locale.setDefault(locale(lang))
    }

    /** 设置里保存的值 → 语言列表中的代码（旧版 zh-CN 等归一）。 */
    fun normalizeChoice(code: String): String = if (code == SYSTEM) SYSTEM else normalize(code)

    fun resolve(code: String): String {
        if (code != SYSTEM) return normalize(code)
        val sys = Resources.getSystem().configuration.locales[0]
        return normalize(sys.toLanguageTag())
    }

    /** 兼容旧设置里的 zh-CN 等写法，未知语言回退英文。 */
    fun normalize(tag: String): String {
        val l = Locale.forLanguageTag(tag)
        return when (l.language) {
            "zh" -> if (l.script == "Hant" || l.country in setOf("TW", "HK", "MO")) "zh-Hant" else "zh-Hans"
            "en", "ja", "ko", "es", "fr", "de", "pt", "ru" -> l.language
            else -> "en"
        }
    }

    fun locale(lang: String): Locale = when (lang) {
        "zh-Hans" -> Locale.SIMPLIFIED_CHINESE
        "zh-Hant" -> Locale.TRADITIONAL_CHINESE
        "pt" -> Locale.forLanguageTag("pt-BR")
        else -> Locale.forLanguageTag(lang)
    }

    val isChinese: Boolean get() = lang.startsWith("zh")

    /** 供 AI 回复语言使用的英文名称。 */
    fun englishName(lang: String = this.lang): String = when (lang) {
        "zh-Hans" -> "Simplified Chinese"; "zh-Hant" -> "Traditional Chinese"; "en" -> "English"
        "ja" -> "Japanese"; "ko" -> "Korean"; "es" -> "Spanish"; "fr" -> "French"
        "de" -> "German"; "pt" -> "Portuguese"; "ru" -> "Russian"; else -> "English"
    }

    fun tr(s: String, lang: String = this.lang): String {
        if (lang == SOURCE || s.isEmpty() || !hasCjk(s)) return s
        val cat = catalog(lang) ?: return s
        return cat.translate(s, lang)
    }

    /** 带参数的模板：tr("{0} 分钟", 5)。 */
    fun tr(template: String, vararg args: Any?): String {
        var out = tr(template)
        args.forEachIndexed { i, a -> out = out.replace("{$i}", a.toString()) }
        return out
    }

    internal fun hasCjk(s: String) = s.any { it in '㐀'..'鿿' }

    private fun catalog(lang: String): Catalog? = catalogs[lang] ?: synchronized(this) {
        catalogs[lang] ?: run {
            val ctx = appContext ?: return null
            val source = read(ctx, "source") ?: return null
            val target = read(ctx, lang) ?: return null
            Catalog(source, target).also { catalogs[lang] = it }
        }
    }

    private fun read(ctx: Context, name: String): List<String>? = runCatching {
        ctx.assets.open("i18n/$name.txt").bufferedReader(Charsets.UTF_8).readLines().map { unescape(it) }
    }.getOrNull()

    private fun unescape(s: String): String {
        if ('\\' !in s) return s
        val b = StringBuilder(s.length)
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '\\' && i + 1 < s.length) {
                when (s[i + 1]) {
                    'n' -> b.append('\n'); '\\' -> b.append('\\'); else -> b.append(s[i + 1])
                }
                i += 2
            } else {
                b.append(c); i++
            }
        }
        return b.toString()
    }

    /** 为测试准备的入口：直接用内存中的条目构造目录。 */
    fun translateWith(source: List<String>, target: List<String>, s: String, lang: String): String =
        if (!hasCjk(s)) s else Catalog(source, target).translate(s, lang)

    private class Template(val regex: Regex, val target: String, val weight: Int)

    private class Catalog(source: List<String>, target: List<String>) {
        private val exact = HashMap<String, String>()
        private val templates: List<Template>
        private val cache = ConcurrentHashMap<String, String>()

        init {
            val tpl = ArrayList<Template>()
            source.forEachIndexed { i, src ->
                val dst = target.getOrNull(i)?.takeIf { it.isNotBlank() } ?: return@forEachIndexed
                if (PH.containsMatchIn(src)) {
                    val parts = PH.split(src)
                    val literal = parts.sumOf { it.length }
                    // 至少要有中文字面量，避免「{0} {1}」这类过于宽泛的模板吞掉一切。
                    if (parts.none { hasCjk(it) }) return@forEachIndexed
                    val names = PH.findAll(src).map { it.groupValues[1] }.toList()
                    val pattern = buildString {
                        append('^')
                        parts.forEachIndexed { k, p ->
                            append(Regex.escape(p))
                            if (k < names.size) append("(?<g").append(names[k]).append(">[\\s\\S]+?)")
                        }
                        append('$')
                    }
                    runCatching { tpl += Template(Regex(pattern), dst, literal) }
                } else {
                    exact[src] = dst
                    // 「 · 夜间曜石」这类拼接片段：拆段后也能按去掉前缀/空白的形式命中。
                    val bare = src.trim().removePrefix("·").trim()
                    if (bare != src && bare.isNotEmpty()) exact.putIfAbsent(bare, dst.trim().removePrefix("·").trim())
                }
            }
            templates = tpl.sortedByDescending { it.weight }
        }

        fun translate(s: String, lang: String): String {
            cache[s]?.let { return it }
            val out = whole(s, lang, 0) ?: affixed(s, lang) ?: segments(s, lang)
            if (cache.size > 4000) cache.clear()
            cache[s] = out
            return out
        }

        private fun whole(s: String, lang: String, depth: Int): String? {
            exact[s]?.let { return it }
            val trimmed = s.trim()
            if (trimmed != s && trimmed.isNotEmpty()) exact[trimmed]?.let { return s.replace(trimmed, it) }
            if (depth > 2) return null
            for (t in templates) {
                val m = t.regex.matchEntire(s) ?: continue
                var out = t.target
                PH.findAll(t.target).map { it.groupValues[1] }.distinct().forEach { n ->
                    val v = m.groups["g$n"]?.value ?: return@forEach
                    val tv = if (hasCjk(v)) whole(v, lang, depth + 1) ?: segments(v, lang) else v
                    out = out.replace("{$n}", tv)
                }
                return out
            }
            return null
        }

        /** 「✓ 今天」「• 设置」这类带符号前后缀的文案：只翻译中间部分。 */
        private fun affixed(s: String, lang: String): String? {
            val m = AFFIX.matchEntire(s) ?: return null
            val (pre, core, post) = m.destructured
            if (pre.isEmpty() && post.isEmpty()) return null
            return whole(core, lang, 0)?.let { pre + it + post }
        }

        /** 按常见分隔符拆段逐段翻译，再把中文标点换成目标语言习惯。 */
        private fun segments(s: String, lang: String): String {
            val pieces = SEP.split(s)
            val seps = SEP.findAll(s).map { it.value }.toList()
            if (pieces.size <= 1) return s
            val b = StringBuilder()
            pieces.forEachIndexed { i, p ->
                b.append(if (hasCjk(p)) whole(p, lang, 1) ?: p else p)
                if (i < seps.size) b.append(punct(seps[i], lang))
            }
            return b.toString().replace(Regex(" {2,}"), " ").trimEnd()
        }

        private fun punct(sep: String, lang: String): String {
            if (lang.startsWith("zh")) return sep
            if (lang == "ja") return when (sep) { "，" -> "、"; "；" -> "。"; else -> sep }
            return when (sep) {
                "，", "、" -> ", "; "。" -> ". "; "：" -> ": "; "；" -> "; "
                "（" -> " ("; "）" -> ")"; "「" -> "“"; "」" -> "”"; "？" -> "? "; "！" -> "! "
                else -> sep
            }
        }
    }

    // Android 的 ICU 正则要求转义右花括号。
    private val PH = Regex("\\{(\\d+)\\}")
    private val AFFIX = Regex("^([^\\p{L}\\p{N}]*)([\\s\\S]*?)([^\\p{L}\\p{N}]*)$")
    private val SEP = Regex(" · | / |，|。|：|；|、|？|！|（|）|「|」|\\n")
}
