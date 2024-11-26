package com.application.mindmate.patient.yourPharmacy

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.application.adapters.ActivitiesRecyclerViewAdapter
import com.application.adapters.MedicinesRecyclerViewAdapter
import com.application.common.ActivityUtils
import com.application.customization.DialogActivity
import com.application.enums.UserRole
import com.application.mindmate.Medicine
import com.application.mindmate.NotificationWorker
import com.application.mindmate.R
import com.application.models.MedicineModel
import com.application.service.ActivitiesService
import com.application.service.MedicinesService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

class YourPharmacyActivity : AppCompatActivity() {
    private lateinit var addMedicineFloatingActionButton: FloatingActionButton
    private lateinit var medicinesService: MedicinesService
    private var medicinesModels: ArrayList<MedicineModel> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicinesRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_pharmacy)
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        medicinesService = MedicinesService()
        addMedicineFloatingActionButton = findViewById(R.id.btn_add_medicine)
        addMedicineFloatingActionButton.setOnClickListener {
            addMedicineDialog()
        }
        recyclerView = findViewById(R.id.recycler_view_medicines)
        getMedicines()
        adapter = MedicinesRecyclerViewAdapter(this, medicinesModels) { medicineName ->
            deleteMedicine(medicineName)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun addMedicineDialog() {
        val v = DialogActivity(this, R.layout.dg_add_item_pharmacy).getDialog()
        val addMedicineButton = v.findViewById<Button>(R.id.btn_save_medicine)
        val timeEditText = v.findViewById<EditText>(R.id.et_time)
        timeEditText.setOnClickListener {
            showTimePickerDialog { time ->
                timeEditText.setText(time)
            }
        }
        addMedicineButton.setOnClickListener {
            val medicineNameEditText = v.findViewById<EditText>(R.id.et_medicine_name)
            val doseEditText = v.findViewById<EditText>(R.id.et_dose)
            val medicineName = medicineNameEditText.text.toString()
            if (checkIfMedicineAlreadyExists(medicineName)) {
                medicinesService.addMedicine(
                    MedicineModel(
                        medicineName,
                        timeEditText.text.toString(),
                        doseEditText.text.toString()
                    )
                )
                adapter.addItem(
                    medicineName,
                    timeEditText.text.toString(),
                    doseEditText.text.toString()
                )
                scheduleNotification(
                    medicineName,
                    timeEditText.text.toString(),
                    doseEditText.text.toString()
                )
                Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "The activity already exists!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMedicines() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val names = medicinesService.fetchMedicineField("name")
                val times = medicinesService.fetchMedicineField("time")
                val doses = medicinesService.fetchMedicineField("dose")
                val newMedicineModels = ArrayList<MedicineModel>()
                for (i in 0..<names.size) {
                    newMedicineModels.add(MedicineModel(names[i], times[i], doses[i]))
                }
                medicinesModels.clear()
                medicinesModels.addAll(newMedicineModels)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteMedicine(medicineName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    medicinesService.removeMedicine(medicineName = medicineName)
                }
                adapter.removeItem(medicineName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkIfMedicineAlreadyExists(medicineName: String): Boolean {
        var medicineNotExists = true
        for (medicine in medicinesModels) {
            if (medicine.getName().lowercase() === medicineName.lowercase()) {
                medicineNotExists = false
            }
        }
        return medicineNotExists
    }

    private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog =
            TimePickerDialog(this, R.style.DialogTheme, { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(time)
            }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun scheduleNotification(medicineName: String, dose: String, time: String) {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        try {
            // Parse the time
            val timeParts = time.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            // Set the notification time
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            // Ensure the time is in the future
            if (calendar.timeInMillis <= currentTime) {
                // Add one day if the time is in the past
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            // Calculate delay
            val delay = calendar.timeInMillis - currentTime

            // Prepare notification data
            val data = Data.Builder()
                .putString("title", "Medicine Reminder")
                .putString("message", "It's time to take your medicine")
                .putString("medicineName", medicineName)
                .putString("dose", dose)
                .build()

            // Schedule the notification
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(this).enqueue(workRequest)
            Log.d("YourPharmacyActivity", "Notification scheduled successfully for $time")

        } catch (e: Exception) {
            Log.e("YourPharmacyActivity", "Error scheduling notification: ${e.message}")
        }
    }
}