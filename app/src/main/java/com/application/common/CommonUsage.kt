package com.application.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommonUsage {
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getDateDifference(firstDateString: String, secondDateString: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val firstDate = sdf.parse(firstDateString)
        val secondDate = sdf.parse(secondDateString)
        val timeDiff = firstDate!!.time - secondDate!!.time
        return kotlin.math.abs(timeDiff / (1000 * 60 * 60 * 24)) % 365 // calculate difference in days
    }
}