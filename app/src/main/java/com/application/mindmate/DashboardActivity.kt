package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class DashboardActivity : AppCompatActivity() {
    private lateinit var openMenu: ImageView
    private lateinit var cognitiveGamesButton: Button
    private lateinit var medicalSurveyButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        openMenu = findViewById(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(this@DashboardActivity, MenuActivity::class.java)
            startActivity(intent)
        }
        cognitiveGamesButton = findViewById(R.id.button_cognitive_games)
        cognitiveGamesButton.setOnClickListener {
            val intent = Intent(this@DashboardActivity, CognitiveGamesActivity::class.java)
            startActivity(intent)
        }
        medicalSurveyButton = findViewById(R.id.button_medical_survey)
        medicalSurveyButton.setOnClickListener {
            val intent = Intent(this@DashboardActivity, MedicalTestActivity::class.java)
            startActivity(intent)
        }
    }
}