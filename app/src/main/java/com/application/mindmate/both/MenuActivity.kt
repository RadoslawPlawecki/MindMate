package com.application.mindmate.both

import android.annotation.SuppressLint
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
import com.application.mindmate.games.CognitiveGamesActivity
import com.application.mindmate.patient.PatientDashboardActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var alarmImageView: ImageView
    private lateinit var goToHomeImageView: ImageView
    private lateinit var goToHomeTextView: TextView
    private lateinit var goToCalendarImageView: ImageView
    private lateinit var goToCalendarTextView: TextView
    private lateinit var signOutTextView: TextView
    private lateinit var signOutImageView: ImageView
    private lateinit var returnBack: ImageView
    private var intentValue= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val intent = intent
        intentValue = intent.getStringExtra("ROLE").toString()
        goToHomeImageView = findViewById(R.id.image_home)
        goToHomeTextView = findViewById(R.id.text_home)
        goToHomeImageView.setOnClickListener(goToHome())
        goToHomeTextView.setOnClickListener(goToHome())
        goToCalendarImageView = findViewById(R.id.image_calendar)
        goToCalendarTextView = findViewById(R.id.text_calendar)
        goToCalendarImageView.setOnClickListener(goToCalendar())
        goToCalendarTextView.setOnClickListener(goToCalendar())
        signOutImageView = findViewById(R.id.image_sign_out)
        signOutTextView = findViewById(R.id.text_sign_out)
        signOutImageView.setOnClickListener(signOut())
        signOutTextView.setOnClickListener(signOut())
        returnBack = findViewById(R.id.image_return)
        alarmImageView = findViewById(R.id.alarm)
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

    private fun goToHome(): View.OnClickListener {
        intent = if (intentValue == UserRole.PATIENT.toString()) {
            Intent(this@MenuActivity, PatientDashboardActivity::class.java)
        } else {
            Intent(this@MenuActivity, CaregiverDashboardActivity::class.java)
        }
        val clickListener = View.OnClickListener {
            startActivity(intent) }
        return clickListener
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

    private fun signOut(): View.OnClickListener {
        val intent = Intent(this@MenuActivity, TitleActivity::class.java)
        val clickListener = View.OnClickListener {
            startActivity(intent)
        }
        return clickListener
    }
}