package com.application.mindmate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adapters.ActivitiesRecyclerViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DailyChecklistActivity : AppCompatActivity() {
    private lateinit var openMenu: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var addActivityFloatingActionButton: FloatingActionButton
    private var activitiesModels: ArrayList<String> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivitiesRecyclerViewAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_checklist)
        openMenu = findViewById(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(this@DailyChecklistActivity, MenuActivity::class.java)
            startActivity(intent)
        }
        titleTextView = findViewById(R.id.daily_checklist_text_view)
        addActivityFloatingActionButton = findViewById(R.id.add_activity_floating)
        addActivityFloatingActionButton.setOnClickListener {
            addActivityDialog()
        }
        recyclerView = findViewById(R.id.recycler_view)
        fetchActivities()
        adapter = ActivitiesRecyclerViewAdapter(this, activitiesModels)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        titleTextView.setOnClickListener {
            Log.d("DailyChecklistActivity", "$activitiesModels")
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun addActivityDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val v = layoutInflater.inflate(R.layout.add_item_checklist, null)
        val addActivityButton = v.findViewById<Button>(R.id.add_activity_buttton)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.create()
        addDialog.show()
        addActivityButton.setOnClickListener {
            val userActivityEditText = v.findViewById<EditText>(R.id.your_activity_edit_text)
            val activity = userActivityEditText.text.toString()
            addActivity(activity)
            Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchActivities() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val activitiesRef = db.collection("users").document(uid).collection("activities").orderBy("id")
            activitiesRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (!documentSnapshot.isEmpty) {
                        val activities = ArrayList<String>()
                        for (document in documentSnapshot.documents) {
                            val activity = document.getString("activity")
                            activity?.let {
                                activities.add(it)
                            }
                        }
                        activitiesModels.clear()
                        activitiesModels.addAll(activities)
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("DailyChecklistActivity", "The document is empty!")
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun addActivity(activity: String) {
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
                            Log.d("DailyChecklistActivity", "Activity added successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("DailyChecklistActivity", "Error adding activity", e)
                        }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }
}
