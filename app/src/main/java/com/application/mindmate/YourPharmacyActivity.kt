package com.application.mindmate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class YourPharmacyActivity : AppCompatActivity() {
    private lateinit var openMenu: ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_checklist)
        openMenu = findViewById(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(this@YourPharmacyActivity, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}