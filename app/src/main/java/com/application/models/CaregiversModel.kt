package com.application.models

import com.application.enums.UserGender
import com.application.enums.UserRole

class CaregiversModel(private var id: Int, private var name: String, private var role: UserRole,
                      private var dateOfBirth: String, private var gender: UserGender,
                      private var lastLoginDate: String, private var streak: Int,
                      private var email: String, private var phoneNumber: Number) {
    fun getId(): Int {
        return id
    }
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
    fun getPhoneNumber(): Number {
        return phoneNumber
    }
}