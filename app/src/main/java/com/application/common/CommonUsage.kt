package com.application.common

import java.text.SimpleDateFormat
import java.util.Calendar
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

    fun calculateAge(dateOfBirth: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthDate = sdf.parse(dateOfBirth) ?: return "Invalid date format"
        val currentDate = Calendar.getInstance()
        val birthDateCalendar = Calendar.getInstance().apply { time = birthDate }
        var age = currentDate.get(Calendar.YEAR) - birthDateCalendar.get(Calendar.YEAR)
        if (currentDate.get(Calendar.DAY_OF_YEAR) < birthDateCalendar.get(Calendar.DAY_OF_YEAR)) {
            age -= 1
        }
        return "$age y.o."
    }

    fun formatPhoneNumber(phoneNumber: String): String {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        return cleanNumber.chunked(3).joinToString(" ")
    }
}