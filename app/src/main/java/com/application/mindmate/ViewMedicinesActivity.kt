package com.application.mindmate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adapters.MedicinesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewMedicinesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
    private lateinit var adapter: MedicinesAdapter
    private val medicinesList = mutableListOf<Medicine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_medicines)

        adapter = MedicinesAdapter(medicinesList)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_medicines)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = db.collection("medicines")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                medicinesList.clear()
                for (document in querySnapshot.documents) {
                    val medicine = document.toObject(Medicine::class.java)?.copy(documentId = document.id)
                    if (medicine != null) {
                        medicinesList.add(medicine)
                    }
                }

                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewMedicinesActivity, "Error fetching medicines: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}