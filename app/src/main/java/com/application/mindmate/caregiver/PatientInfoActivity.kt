package com.application.mindmate.caregiver

import PatientsService
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.common.CommonUsage
import com.application.enums.UserRole
import com.application.mindmate.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatientInfoActivity : AppCompatActivity() {
    private lateinit var patientsService: PatientsService
    private lateinit var patientInfoLinearLayout: LinearLayout
    private lateinit var patientNameTextView: TextView
    private lateinit var patientGenderTextView: TextView
    private lateinit var patientAgeTextView: TextView
    private lateinit var patientEmailTextView: TextView
    private lateinit var patientLastLoginTextView: TextView
    private lateinit var externalProgressBar: ProgressBar
    private lateinit var internalProgressBar: ProgressBar
    private lateinit var loadingDataTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_info)
        ActivityUtils.actionBarSetup(this, UserRole.CAREGIVER)
        patientsService = PatientsService()
        val patientId = intent.getStringExtra("PATIENT_ID") ?: run {
            finish()
            return
        }
        patientInfoLinearLayout = findViewById(R.id.linear_layout_patient_info)
        patientNameTextView = findViewById(R.id.text_patient_name)
        patientGenderTextView = findViewById(R.id.text_patient_gender)
        patientAgeTextView = findViewById(R.id.text_patient_age)
        patientEmailTextView = findViewById(R.id.text_patient_email)
        patientLastLoginTextView = findViewById(R.id.text_patient_lld)
        externalProgressBar = findViewById(R.id.external_progress_bar)
        internalProgressBar = findViewById(R.id.internal_progress_bar)
        loadingDataTextView = findViewById(R.id.loading_data_text)
        CoroutineScope(Dispatchers.Main).launch {
            val patientData = patientsService.fetchPatientById(patientId)
            if (patientData != null) {
                patientNameTextView.text = patientData["name"]
                patientGenderTextView.text = patientData["gender"]
                patientAgeTextView.text = CommonUsage.calculateAge(patientData["dateOfBirth"] ?: "")
                patientEmailTextView.text = patientData["email"]
                patientLastLoginTextView.text = patientData["lastLoginDate"]
            } else {
                finish()
            }
            closeLoading()
        }
    }

    private fun closeLoading() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                loadingDataTextView.visibility = View.GONE
                internalProgressBar.visibility = View.GONE
                externalProgressBar.visibility = View.GONE
                patientInfoLinearLayout.visibility = View.VISIBLE
                patientInfoLinearLayout.startAnimation(fadeIn)
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        loadingDataTextView.startAnimation(fadeOut)
        internalProgressBar.startAnimation(fadeOut)
        externalProgressBar.startAnimation(fadeOut)
    }
}