package com.application.entries

data class MedicalTestEntry(
    val name: String,
    val dateOfBirth: String,
    val school: String,
    val date: String,
    val similarity: String,
    val coins: String,
    val change: String,
    val animalNames: String,
    val submissionDate: String, // Add this field
    val userId: String? = null // Add this field
)

