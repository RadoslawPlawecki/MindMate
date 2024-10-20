package com.application.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object InputValidation {
    fun isEmailValid(text: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return emailRegex.matches(text.trim{ it <= ' '}) && text.count { it == '@' } == 1
    }
    fun isPhoneNumberValid(text: String): Boolean {
        val phoneRegex = "^[0-9]+$".toRegex()
        return phoneRegex.matches(text.trim{ it <= ' '})
    }
    fun isNameValid(text: String): Boolean {
        val nameRegex = "^[A-Za-z\\s-]+$".toRegex()
        return nameRegex.matches(text.trim { it <= ' ' })
    }
    fun isDateOfBirthValid(dateOfBirth: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            val dob = dateFormat.parse(dateOfBirth)
            val currentDate = Calendar.getInstance().time
            !dob!!.after(currentDate)
        } catch (e: Exception) {
            false
        }
    }
    fun isPasswordValid(text: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$".toRegex()
        return passwordRegex.matches(text.trim { it <= ' ' })
    }
    fun isTextMatching(firstText: String, secondText: String): Boolean {
        return firstText == secondText
    }
}