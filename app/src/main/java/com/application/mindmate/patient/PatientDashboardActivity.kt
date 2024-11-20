package com.application.mindmate.patient

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.common.CommonUsage
import com.application.enums.UserRole
import com.application.mindmate.games.CognitiveGamesActivity
import com.application.mindmate.DailyChecklistActivity
import com.application.mindmate.MedicalTestActivity
import com.application.mindmate.R
import com.application.mindmate.YourPharmacyActivity
import com.application.service.CaregiversService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PatientDashboardActivity : AppCompatActivity() {
    private lateinit var helloTextView: TextView
    private lateinit var daysOfUseTextView: TextView
    private lateinit var caregiverNameTextView: TextView
    private lateinit var caregiverInfoImageView: ImageView
    private lateinit var caregiversService: CaregiversService
    private lateinit var caregiverId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)
        caregiversService = CaregiversService()
        helloTextView = findViewById(R.id.hello)
        daysOfUseTextView = findViewById(R.id.days_of_use)
        caregiverNameTextView = findViewById(R.id.text_caregiver_name)
        caregiverInfoImageView = findViewById(R.id.ic_caregiver_info)
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        ActivityUtils.changeActivity<LinearLayout>(R.id.button_cognitive_games, this, CognitiveGamesActivity())
        ActivityUtils.changeActivity<LinearLayout>(R.id.button_medical_survey, this, MedicalTestActivity())
        ActivityUtils.changeActivity<LinearLayout>(R.id.button_your_pharmacy, this, YourPharmacyActivity())
        ActivityUtils.changeActivity<LinearLayout>(R.id.button_daily_checklist, this, DailyChecklistActivity())
        CoroutineScope(Dispatchers.Main).launch {
            helloUser()
            displayUserStreak()
            caregiverId = getCaregiverId().toString()
            caregiverNameTextView.text = caregiversService.fetchCaregiverField(caregiverId, "name")
        }
        caregiverInfoImageView.setOnClickListener {
            val intent = Intent(this, CaregiverInfoActivity::class.java)
            intent.putExtra("CAREGIVER_ID", caregiverId)
            startActivity(intent)
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

    private suspend fun getCaregiverId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("patients").document(uid)
            return try {
                val documentSnapshot = withContext(Dispatchers.IO) {
                    userRef.get().await()
                }
                if (documentSnapshot.exists()) {
                    documentSnapshot.getString("caregiverId")
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return null
    }
}