package io.github.composefluent.component

import platform.Foundation.NSCalendar

/**
 * Very short standalone names ("S", "M", "T"…), matching the `NARROW_STANDALONE` style the
 * JVM and Android targets use, so a 7-column grid header lines up on every platform. The
 * previous implementation returned `weekdaySymbols`, i.e. the full names ("Sunday"), which
 * cannot fit a calendar cell.
 *
 * The array is 0-indexed starting at Sunday, which is the order the common code expects.
 */
internal actual fun getLocalDayOfWeekNames(): List<String> =
    NSCalendar.currentCalendar.veryShortStandaloneWeekdaySymbols.map { it.toString() }

/**
 * Abbreviated standalone month names ("Jan", "Feb"…), matching `SHORT_STANDALONE` on the JVM
 * and Android. This replaces `NSDateFormatter().monthSymbols` truncated with `take(3)`, which
 * cut full names at three characters — wrong for any language whose abbreviation is not simply
 * the first three letters.
 *
 * "Standalone" matters for inflected languages such as Russian, where a month shown on its own
 * takes a different form than inside a formatted date.
 */
internal actual fun getLocalMonthNames(): List<String> =
    NSCalendar.currentCalendar.shortStandaloneMonthSymbols.map { it.toString() }

/**
 * Reads the user's own calendar preferences. `currentCalendar` follows the region set in
 * Settings, and `firstWeekday` is an `NSUInteger` already numbered Sunday(1)..Saturday(7) —
 * exactly the contract of the expect declaration, so no remapping is needed. It is exposed
 * as `ULong` on 64-bit Apple targets, hence the `toInt()`.
 *
 * This used to return a hard-coded `2`, which forced every device to start the week on
 * Monday regardless of region (wrong for the US, Japan, Israel and others).
 *
 * Like the JVM and the browser, Apple sources this from CLDR, so all targets agree modulo
 * the CLDR version each platform ships.
 */
internal actual fun getLocalFirstDayOfWeek(): Int =
    NSCalendar.currentCalendar.firstWeekday.toInt()
