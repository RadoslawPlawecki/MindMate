package com.application.service

import android.util.Log
import com.application.common.CommonUsage
import com.application.enums.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CaregiversService {
    suspend fun addCaregiver(firebaseUser: FirebaseUser, name: String, dateOfBirth: String,
                           caregiverEmail: String, gender: String, phoneNumber: Number) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            try {
                val role = UserRole.USER
                val patientData = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "email" to caregiverEmail,
                    "phoneNumber" to phoneNumber,
                    "name" to name,
                    "dateOfBirth" to dateOfBirth,
                    "gender" to gender,
                    "role" to role,
                    "lastLoginDate" to CommonUsage.getCurrentDate(),
                    "streak" to 1,
                )
                db.collection("caregivers").document(firebaseUser.uid).set(patientData).await()
                Log.d("RegisterActivity", "Document added: ${firebaseUser.uid}")
                FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error adding document: ${e.message}")
            }
        }
    }
}