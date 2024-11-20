package com.application.service

import android.util.Log
import com.application.common.CommonUsage
import com.application.enums.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CaregiversService {
    suspend fun addCaregiver(firebaseUser: FirebaseUser, name: String, dateOfBirth: String,
                           caregiverEmail: String, gender: String, caregiverPhone: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            try {
                val role = UserRole.CAREGIVER
                val patientData = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "email" to caregiverEmail,
                    "name" to name,
                    "dateOfBirth" to dateOfBirth,
                    "gender" to gender,
                    "role" to role,
                    "lastLoginDate" to CommonUsage.getCurrentDate(),
                    "streak" to 1,
                    "phoneNumber" to caregiverPhone
                )
                db.collection("caregivers").document(firebaseUser.uid).set(patientData).await()
                Log.d("SignUpInformationActivity", "Document added: ${firebaseUser.uid}")
                FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                Log.e("SignUpInformationActivity", "Error adding document: ${e.message}")
            }
        }
    }

    suspend fun fetchCaregiverField(caregiverId: String, field: String): String {
        val db = FirebaseFirestore.getInstance()
        return try {
            val document = db.collection("caregivers")
                .document(caregiverId)
                .get()
                .await()
            if (document.exists()) {
                document.getString(field) ?: "Caregiver $field not available!"
            } else {
                "Caregiver not found!"
            }
        } catch (e: Exception) {
            "Error fetching caregiver name: ${e.localizedMessage}"
        }
    }
}