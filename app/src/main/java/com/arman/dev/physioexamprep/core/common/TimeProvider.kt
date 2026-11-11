package com.arman.dev.physioexamprep.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/** Source of "now", so time-dependent logic can be tested. */
interface TimeProvider {
    /** Unix epoch milliseconds. */
    fun nowMillis(): Long

    /** Local calendar date of [epochMillis] as YYYY-MM-DD. */
    fun localDate(epochMillis: Long): String
}

class SystemTimeProvider @Inject constructor() : TimeProvider {
    override fun nowMillis(): Long = System.currentTimeMillis()

    // java.time needs API 26 (minSdk is 24), so use SimpleDateFormat (new instance: not thread-safe).
    override fun localDate(epochMillis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .apply { timeZone = TimeZone.getDefault() }
            .format(Date(epochMillis))
}
