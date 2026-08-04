package io.github.composefluent.component

internal actual fun getLocalDayOfWeekNames(): List<String> {
    val jsFun: String = js(
        """
            var format = new Intl.DateTimeFormat(navigator.language, { weekday: 'short' })
            var baseDate = new Date(Date.UTC(2017, 0, 1)) // just a Sunday
            var weekDays = []
            for (var day = 0; day < 7; day++) {      
                weekDays.push(format.format(baseDate))
                baseDate.setDate(baseDate.getDate() + 1)
            }
            weekDays.join(",")
        """
    )

    return jsFun.split(",")
}

internal actual fun getLocalMonthNames(): List<String> {
    val jsFun: String = js(
        """
            var format = new Intl.DateTimeFormat(navigator.language, { month: 'short' })
            var months = []
            for (var month = 0; month < 12; month++) {
                var testDate = new Date(Date.UTC(2000, month, 1, 0, 0, 0))
                months.push(format.format(testDate))
            }
            months.join(",")
        """
    )

    return jsFun.split(",")
}

/**
 * Reads the browser's own CLDR week data.
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/Locale/getWeekInfo
 *
 * Two shapes of this API exist in the wild and both are probed:
 *  - `getWeekInfo()`, the standard method — Chrome 130+, Firefox 153+, Safari 17+;
 *  - `weekInfo`, the earlier accessor property — Chrome 99..129 and older WebKit.
 *
 * It was changed from accessor to method because it returns a fresh object per access. Calling
 * only the method throws across that whole earlier band, which previously sent those browsers
 * down the fallback path even though they had the data.
 *
 * `maximize()` adds likely subtags, so a region-less `navigator.language` such as `"fr"` still
 * resolves against FR instead of the world default — the week data is keyed by region.
 *
 * `firstDay` is ISO-numbered 1=Monday..7=Sunday and may be 1, 5, 6 or 7; `% 7 + 1` maps it onto
 * this module's Sunday(1)..Saturday(7) contract (Mon 1->2, Fri 5->6, Sat 6->7, Sun 7->1).
 */
internal actual fun getLocalFirstDayOfWeek(): Int =
    js(
        """
            var result = null
            try {
                var locale = new Intl.Locale(navigator.language).maximize()
                var info = typeof locale.getWeekInfo === 'function' ? locale.getWeekInfo() : locale.weekInfo
                if (info && info.firstDay) result = info.firstDay % 7 + 1
            } catch(e) { }
            result
        """
    ) as Int? ?: FALLBACK_FIRST_DAY_OF_WEEK