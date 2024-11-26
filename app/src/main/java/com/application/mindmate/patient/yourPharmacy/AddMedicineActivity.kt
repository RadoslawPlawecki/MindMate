package com.application.mindmate.patient.yourPharmacy

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.application.common.ActivityUtils
import com.application.enums.UserRole
import com.application.mindmate.NotificationWorker
import com.application.mindmate.databinding.ActivityAddMedicineBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMedicineBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        binding.etTime.setOnClickListener {
            showTimePickerDialog { time ->
                binding.etTime.setText(time)
            }
        }

        binding.btnSaveMedicine.setOnClickListener {
            val name = binding.etMedicineName.text.toString().trim()
            val dose = binding.etDose.text.toString().trim()
            val time = binding.etTime.text.toString().trim()

            if (name.isNotEmpty() && dose.isNotEmpty() && time.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val medicine = hashMapOf(
                            "name" to name,
                            "dose" to dose,
                            "time" to time,
                            "userId" to userId
                        )
                        val documentRef = db.collection("medicines").add(medicine).await()
                        scheduleNotification(name, dose, time)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddMedicineActivity, "Medicine added", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddMedicineActivity, "Error adding medicine: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun scheduleNotification(medicineName: String, dose: String, time: String) {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        val timeParts = time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        val delay = if (calendar.timeInMillis > currentTime) {
            calendar.timeInMillis - currentTime
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.timeInMillis - currentTime
        }

        val data = Data.Builder()
            .putString("title", "Medicine Reminder")
            .putString("message", "It's time to take your medicine")
            .putString("medicineName", medicineName)
            .putString("dose", dose)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}