package io.github.composefluent.component

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSDateFormatter

internal actual fun getLocalDayOfWeekNames(): List<String> {
    return NSCalendar(NSCalendarIdentifierGregorian).weekdaySymbols.map { it.toString() }
}

internal actual fun getLocalMonthNames(): List<String> {
    return NSDateFormatter().monthSymbols.map { it.toString().take(3) } //TODO
}

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
