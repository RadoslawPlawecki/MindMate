package com.application.models

class MedicineModel(private var name: String, private var time: String, private var dose: String) {
    fun getName(): String {
        return name
    }
    fun getTime(): String {
        return time
    }
    fun getDose(): String {
        return dose
    }
}