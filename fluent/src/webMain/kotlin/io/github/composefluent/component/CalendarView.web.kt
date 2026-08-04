package io.github.composefluent.component

/**
 * Week start assumed when the browser exposes no week information at all.
 *
 * Monday: the ISO 8601 default, and the majority answer worldwide — CLDR groups roughly 190 of
 * its 250 territories under Monday.
 *
 * This replaces a hand-written 101-entry locale table. A table in library code is the wrong
 * shape for this data: it silently goes stale as conventions and CLDR change, it was keyed on
 * full language tags so a bare `"fr"` from `navigator.language` missed every entry and landed
 * on the default anyway, and it disagreed with what the JVM and Apple targets reported for the
 * same locale. The browser already ships CLDR; the job is to read it properly rather than to
 * duplicate it — see `getLocalFirstDayOfWeek` in the js and wasmJs source sets.
 *
 * Engines that still need this fallback: Firefox before 153, Firefox for Android up to 152, and
 * Safari before 17.
 */
internal const val FALLBACK_FIRST_DAY_OF_WEEK = 2
