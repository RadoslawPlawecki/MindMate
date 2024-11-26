package com.application.service

import android.util.Log
import com.application.models.MedicineModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MedicinesService {
    suspend fun removeMedicine(medicineName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val activitiesRef = db.collection("patients").document(uid).collection("medicines")
            try {
                val querySnapshot = activitiesRef.whereEqualTo("name", medicineName).get().await()
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        document.reference.delete().await()
                        Log.d("MedicinesService", "Medicine removed successfully!")
                    }
                } else {
                    Log.e("MedicinesService", "No medicine found with the given name")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addMedicine(model: MedicineModel) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val activitiesRef = db.collection("patients").document(uid).collection("medicines")
            activitiesRef.orderBy("id").get()
                .addOnSuccessListener { querySnapshot ->
                    val nextId = if (querySnapshot.isEmpty) {
                        1
                    } else {
                        querySnapshot.documents.size + 1
                    }
                    val newActivity = hashMapOf(
                        "id" to nextId,
                        "name" to model.getName(),
                        "time" to model.getTime(),
                        "dose" to model.getDose()
                    )
                    activitiesRef.document().set(newActivity)
                        .addOnSuccessListener {
                            Log.d("MedicinesService", "Medicine added successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MedicinesService", "Error adding activity", e)
                        }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    suspend fun fetchMedicineField(field: String): ArrayList<String> {
        val fieldValues = ArrayList<String>()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                val uid = user.uid
                val db = FirebaseFirestore.getInstance()
                val querySnapshot = db.collection("patients")
                    .document(uid)
                    .collection("medicines")
                    .get()
                    .await()

                for (document in querySnapshot.documents) {
                    val value = document.getString(field)
                    value?.let {
                        fieldValues.add(it)
                    }
                }
            } catch (e: Exception) {
            }
        }
        return fieldValues
    }
}