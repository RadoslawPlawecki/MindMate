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

class PatientsService {
    suspend fun addPatient(firebaseUser: FirebaseUser, name: String, dateOfBirth: String,
                           caregiverEmail: String, gender: String, patientEmail: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val caregiverId = getCaregiverByEmail(db, caregiverEmail)
            if (caregiverId != null) {
                try {
                    val role = UserRole.USER
                    val patientData = hashMapOf(
                        "uid" to firebaseUser.uid,
                        "email" to patientEmail,
                        "name" to name,
                        "dateOfBirth" to dateOfBirth,
                        "gender" to gender,
                        "role" to role,
                        "lastLoginDate" to CommonUsage.getCurrentDate(),
                        "streak" to 1,
                        "caregiverId" to caregiverId
                    )
                    db.collection("patients").document(firebaseUser.uid).set(patientData).await()
                    Log.d("RegisterActivity", "Document added: ${firebaseUser.uid}")
                    FirebaseAuth.getInstance().signOut()
                } catch (e: Exception) {
                    Log.e("RegisterActivity", "Error adding document: ${e.message}")
                }
            } else {
                Log.e("PatientsService", "Caregiver not found for email: $caregiverEmail")
            }
        }
    }

    private suspend fun getCaregiverByEmail(db: FirebaseFirestore, caregiverEmail: String): String? {
        return try {
            val querySnapshot = db.collection("caregivers")
                .whereEqualTo("email", caregiverEmail)
                .get()
                .await()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents.first().id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("PatientsService", "Error fetching caregiverID: ${e.message}")
            null
        }
    }
}