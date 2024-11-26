package com.application.mindmate.both

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.application.enums.UserRole
import com.application.mindmate.AlarmActivity
import com.application.mindmate.R
import com.application.mindmate.caregiver.CaregiverDashboardActivity
import com.application.mindmate.caregiver.CaregiverSettingsActivity
import com.application.mindmate.patient.PatientDashboardActivity
import com.application.mindmate.patient.PatientSettingsActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var alarmImageView: ImageView
    private lateinit var goToHomeImageView: ImageView
    private lateinit var goToHomeTextView: TextView
    private lateinit var goToCalendarImageView: ImageView
    private lateinit var goToCalendarTextView: TextView
    private lateinit var goToSettingsImageView: ImageView
    private lateinit var goToSettingsTextView: TextView
    private lateinit var signOutTextView: TextView
    private lateinit var signOutImageView: ImageView
    private lateinit var returnBack: ImageView
    private var intentValue= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val intent = intent
        alarmImageView = findViewById(R.id.alarm)
        intentValue = intent.getStringExtra("ROLE").toString()
        if (intentValue == UserRole.CAREGIVER.toString()) {
            alarmImageView.visibility = View.GONE
        }
        goToHomeImageView = findViewById(R.id.image_home)
        goToHomeTextView = findViewById(R.id.text_home)
        goToHomeImageView.setOnClickListener(goToHome())
        goToHomeTextView.setOnClickListener(goToHome())
        goToCalendarImageView = findViewById(R.id.image_calendar)
        goToCalendarTextView = findViewById(R.id.text_calendar)
        goToCalendarImageView.setOnClickListener(goToCalendar())
        goToCalendarTextView.setOnClickListener(goToCalendar())
        goToSettingsImageView = findViewById(R.id.image_settings)
        goToSettingsTextView = findViewById(R.id.text_settings)
        goToSettingsImageView.setOnClickListener(goToSettings())
        goToSettingsTextView.setOnClickListener(goToSettings())
        signOutImageView = findViewById(R.id.image_sign_out)
        signOutTextView = findViewById(R.id.text_sign_out)
        signOutImageView.setOnClickListener(signOut())
        signOutTextView.setOnClickListener(signOut())
        returnBack = findViewById(R.id.image_return)
        alarmImageView.setOnClickListener {
            AlarmActivity(this).useAlarm()
        }
        returnBack.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun goToCalendar(): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "Event Title") // Optional: set default title
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
                putExtra(
                    CalendarContract.EXTRA_EVENT_END_TIME,
                    System.currentTimeMillis() + 60 * 60 * 1000 // 1 hour later
                )
            }
            val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
            if (resolveInfoList.isNotEmpty()) {
                val chooser = Intent.createChooser(intent, "Choose Calendar App")
                startActivity(chooser)
            } else {
                Toast.makeText(this, "No calendar app found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToHome(): View.OnClickListener {
        val homeIntent = if (intentValue == UserRole.PATIENT.toString()) {
            Intent(this@MenuActivity, PatientDashboardActivity::class.java)
        } else {
            Intent(this@MenuActivity, CaregiverDashboardActivity::class.java)
        }
        return View.OnClickListener {
            startActivity(homeIntent)
        }
    }

    private fun goToSettings(): View.OnClickListener {
        val settingsIntent = if (intentValue == UserRole.PATIENT.toString()) {
            Intent(this@MenuActivity, PatientSettingsActivity::class.java)
        } else {
            Intent(this@MenuActivity, CaregiverSettingsActivity::class.java)
        }
        return View.OnClickListener {
            startActivity(settingsIntent)
        }
    }

    private fun signOut(): View.OnClickListener {
        val intent = Intent(this@MenuActivity, TitleActivity::class.java)
        val clickListener = View.OnClickListener {
            startActivity(intent)
        }
        return clickListener
    }
}