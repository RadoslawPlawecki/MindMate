package com.application.mindmate.patient

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
import com.application.service.CaregiversService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaregiverInfoActivity : AppCompatActivity() {
    private lateinit var caregiversService: CaregiversService
    private lateinit var caregiverId: String
    private lateinit var caregiverInfoLinearLayout: LinearLayout
    private lateinit var caregiverNameTextView: TextView
    private lateinit var caregiverGenderTextView: TextView
    private lateinit var caregiverAgeTextView: TextView
    private lateinit var caregiverPhoneNumberTextView: TextView
    private lateinit var caregiverEmailTextView: TextView
    private lateinit var caregiverLastLoginTextView: TextView
    private lateinit var externalProgressBar: ProgressBar
    private lateinit var internalProgressBar: ProgressBar
    private lateinit var loadingDataTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_info)
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        caregiversService = CaregiversService()
        caregiverId = intent.getStringExtra("CAREGIVER_ID").toString()
        caregiverInfoLinearLayout = findViewById(R.id.linear_layout_caregiver_info)
        caregiverNameTextView = findViewById(R.id.text_caregiver_name)
        caregiverGenderTextView = findViewById(R.id.text_caregiver_gender)
        caregiverAgeTextView = findViewById(R.id.text_caregiver_age)
        caregiverPhoneNumberTextView = findViewById(R.id.text_caregiver_phone)
        caregiverEmailTextView = findViewById(R.id.text_caregiver_email)
        caregiverLastLoginTextView = findViewById(R.id.text_caregiver_lld)
        externalProgressBar = findViewById(R.id.external_progress_bar)
        internalProgressBar = findViewById(R.id.internal_progress_bar)
        loadingDataTextView = findViewById(R.id.loading_data_text)
        CoroutineScope(Dispatchers.Main).launch {
            caregiverNameTextView.text = caregiversService.fetchCaregiverField(caregiverId, "name")
            caregiverGenderTextView.text = caregiversService.fetchCaregiverField(caregiverId, "gender")
            caregiverAgeTextView.text = CommonUsage.calculateAge(caregiversService.fetchCaregiverField(caregiverId, "dateOfBirth"))
            caregiverPhoneNumberTextView.text = CommonUsage.formatPhoneNumber(caregiversService.fetchCaregiverField(caregiverId, "phoneNumber"))
            caregiverEmailTextView.text = caregiversService.fetchCaregiverField(caregiverId, "email")
            caregiverLastLoginTextView.text = caregiversService.fetchCaregiverField(caregiverId, "lastLoginDate")
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
                caregiverInfoLinearLayout.visibility = View.VISIBLE
                caregiverInfoLinearLayout.startAnimation(fadeIn)
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        loadingDataTextView.startAnimation(fadeOut)
        internalProgressBar.startAnimation(fadeOut)
        externalProgressBar.startAnimation(fadeOut)
    }
}