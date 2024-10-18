package com.application.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommonUsage {
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}