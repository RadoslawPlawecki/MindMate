package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.application.mindmate.both.LoginActivity
import com.google.firebase.FirebaseApp

class TitleActivity : AppCompatActivity() {
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
        FirebaseApp.initializeApp(this)
        button = findViewById(R.id.getting_started)
        button.setOnClickListener {
            val intent = Intent(this@TitleActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}