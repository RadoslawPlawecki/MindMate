package com.application.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ActivitiesService {

    suspend fun removeActivity(activityName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val activitiesRef = db.collection("users").document(uid).collection("activities")
            try {
                val querySnapshot = activitiesRef.whereEqualTo("activity", activityName).get().await()
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        document.reference.delete().await()
                        Log.d("ActivitiesService", "Activity removed successfully!")
                    }
                } else {
                    Log.e("ActivitiesService", "No activity found with the given name")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addActivity(activity: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val activitiesRef = db.collection("users").document(uid).collection("activities")
            activitiesRef.orderBy("id").get()
                .addOnSuccessListener { querySnapshot ->
                    val nextId = if (querySnapshot.isEmpty) {
                        1
                    } else {
                        querySnapshot.documents.size + 1
                    }
                    val newActivity = hashMapOf(
                        "id" to nextId,
                        "activity" to activity,
                    )
                    activitiesRef.document().set(newActivity)
                        .addOnSuccessListener {
                            Log.d("ActivitiesService", "Activity added successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ActivitiesService", "Error adding activity", e)
                        }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    suspend fun fetchActivities(): ArrayList<String> {
        val activities = ArrayList<String>()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val activitiesRef = db.collection("users").document(uid).collection("activities").orderBy("id")
            try {
                val documentSnapshot = activitiesRef.get().await()
                if (!documentSnapshot.isEmpty) {
                    for (document in documentSnapshot.documents) {
                        val activity = document.getString("activity")
                        activity?.let {
                            activities.add(it)
                        }
                    }
                } else {
                    Log.e("ActivitiesService", "The document is empty!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return activities
    }
}