package com.application.mindmate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adapters.ActivitiesRecyclerViewAdapter
import com.application.common.ActivityUtils
import com.application.customization.DialogActivity
import com.application.service.ActivitiesService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DailyChecklistActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var activitiesService: ActivitiesService
    private lateinit var addActivityFloatingActionButton: FloatingActionButton
    private var activitiesModels: ArrayList<String> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivitiesRecyclerViewAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_checklist)
        ActivityUtils.actionBarSetup(this)
        activitiesService = ActivitiesService()  // initialize activitiesService
        titleTextView = findViewById(R.id.daily_checklist_text_view)
        addActivityFloatingActionButton = findViewById(R.id.add_activity_floating)
        addActivityFloatingActionButton.setOnClickListener {
            addActivityDialog()
        }
        recyclerView = findViewById(R.id.recycler_view)
        getActivities()  // get activities in the database
        adapter = ActivitiesRecyclerViewAdapter(this, activitiesModels) { activityName ->
            deleteActivity(activityName)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        titleTextView.setOnClickListener {
            Log.d("DailyChecklistActivity", "$activitiesModels")
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun addActivityDialog() {
        val v = DialogActivity(this, R.layout.add_item_checklist).getDialog()
        val addActivityButton = v.findViewById<Button>(R.id.add_activity_buttton)
        addActivityButton.setOnClickListener {
            val userActivityEditText = v.findViewById<EditText>(R.id.your_activity_edit_text)
            val activityName = userActivityEditText.text.toString()
            if (checkIfActivityAlreadyExists(activityName)) {
                activitiesService.addActivity(activityName)
                adapter.addItem(activityName)
                Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "The activity already exists!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getActivities() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val activities = withContext(Dispatchers.IO) {
                    activitiesService.fetchActivities()
                }
                activitiesModels.clear()
                activitiesModels.addAll(activities)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteActivity(activityName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    activitiesService.removeActivity(activityName)
                }
                adapter.removeItem(activityName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkIfActivityAlreadyExists(activityName: String): Boolean {
        var activityNotExists = true
        for (activity in activitiesModels) {
            if (activity.lowercase() == activityName.lowercase()) {
                activityNotExists = false
            }
        }
        return activityNotExists
    }
}
