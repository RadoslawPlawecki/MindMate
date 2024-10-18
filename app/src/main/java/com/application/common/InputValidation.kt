package com.application.common

object InputValidation {
    fun isEmailValid(text: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return emailRegex.matches(text.trim{ it <= ' '}) && text.count { it == '@' } == 1
    }
    fun isPhoneNumberValid(text: String): Boolean {
        val phoneRegex = "^[0-9]+$".toRegex()
        return phoneRegex.matches(text.trim{ it <= ' '})
    }
}