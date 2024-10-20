package com.application.mindmate.patient

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.common.CommonUsage
import com.application.mindmate.CognitiveGamesActivity
import com.application.mindmate.DailyChecklistActivity
import com.application.mindmate.MedicalTestActivity
import com.application.mindmate.R
import com.application.mindmate.YourPharmacyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PatientDashboardActivity : AppCompatActivity() {
    private lateinit var cognitiveGamesButton: Button
    private lateinit var medicalTestButton: Button
    private lateinit var yourPharmacyButton: Button
    private lateinit var dailyChecklistButton: Button
    private lateinit var helloTextView: TextView
    private lateinit var daysOfUseTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        helloTextView = findViewById(R.id.hello)
        daysOfUseTextView = findViewById(R.id.days_of_use)
        ActivityUtils.actionBarSetup(this)
        cognitiveGamesButton = findViewById(R.id.button_cognitive_games)
        cognitiveGamesButton.setOnClickListener {
            val intent = Intent(this@PatientDashboardActivity, CognitiveGamesActivity::class.java)
            startActivity(intent)
        }
        medicalTestButton = findViewById(R.id.button_medical_survey)
        medicalTestButton.setOnClickListener {
            val intent = Intent(this@PatientDashboardActivity, MedicalTestActivity::class.java)
            startActivity(intent)
        }
        yourPharmacyButton = findViewById(R.id.button_your_pharmacy)
        yourPharmacyButton.setOnClickListener {
            val intent = Intent(this@PatientDashboardActivity, YourPharmacyActivity::class.java)
            startActivity(intent)
        }
        dailyChecklistButton = findViewById(R.id.button_daily_checklist)
        dailyChecklistButton.setOnClickListener {
            val intent = Intent(this@PatientDashboardActivity, DailyChecklistActivity::class.java)
            startActivity(intent)
        }
        CoroutineScope(Dispatchers.Main).launch {
            helloUser()
            displayUserStreak()
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun helloUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("patients").document(uid)
            try {
                val documentSnapshot = withContext(Dispatchers.IO) {
                    userRef.get().await()
                }
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name") ?: "User"
                    helloTextView.text = "Hello $name!"
                } else {
                    helloTextView.text = "Hello User!"
                }
            } catch (e: Exception) {
                helloTextView.text = "Hello User!"
                e.printStackTrace()
            }
        }
    }

    private suspend fun displayUserStreak() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("patients").document(uid)
            try {
                val documentSnapshot = withContext(Dispatchers.IO) {
                    userRef.get().await()
                }
                if (documentSnapshot.exists()) {
                    val lastLoginDate = documentSnapshot.getString("lastLoginDate")
                    val streak = documentSnapshot.getLong("streak")?.toInt() ?: 0
                    val currentDate = CommonUsage.getCurrentDate()
                    val dateDiffInDays = CommonUsage.getDateDifference(lastLoginDate.toString(), currentDate)
                    val updatedStreak = when (dateDiffInDays) {
                        0.toLong() -> {
                            streak
                        }
                        1.toLong() -> {
                            streak + 1
                        }
                        else -> {
                            1
                        }
                    }
                    if (lastLoginDate != currentDate) {
                        userRef.update("lastLoginDate", currentDate, "streak", updatedStreak).await()
                    }
                    val streakText = if (updatedStreak != 1) {
                        getString(R.string.you_ve_been_using_mindmate_for_x_days_in_a_row, updatedStreak)
                    } else {
                        getString(R.string.you_ve_been_using_mindmate_for_1_day, updatedStreak)
                    }
                    daysOfUseTextView.text = streakText
                } else {
                    val initialStreak = 1
                    val currentDate = CommonUsage.getCurrentDate()
                    userRef.set(mapOf("lastLoginDate" to currentDate, "streak" to initialStreak)).await()
                    val streakText = getString(R.string.you_ve_been_using_mindmate_for_1_day, initialStreak)
                    daysOfUseTextView.text = streakText
                }
            } catch (e: Exception) {
                daysOfUseTextView.text = getString(R.string.you_ve_been_using_mindmate_for_1_day, 1)
                e.printStackTrace()
            }
        }
    }
}