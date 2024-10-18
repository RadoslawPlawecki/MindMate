package com.application.models

import com.application.enums.UserGender
import com.application.enums.UserRole

class PatientsModel(private var name: String, private var role: UserRole,
                    private var dateOfBirth: String, private var gender: UserGender,
                    private var lastLoginDate: String, private var streak: Int,
                    private var email: String, private var caregiverId: Int) {
    fun getName(): String {
        return name
    }
    fun getRole(): String {
        return role.toString()
    }
    fun getDateOfBirth(): String {
        return dateOfBirth
    }
    fun getGender(): String {
        return gender.toString()
    }
    fun getLastLoginDate(): String {
        return lastLoginDate
    }
    fun getStreak(): Int {
        return streak
    }
    fun getEmail(): String {
        return email
    }
    fun getCaregiverId(): Int {
        return caregiverId
    }
}