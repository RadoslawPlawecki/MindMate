package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.application.mindmate.both.LoginActivity

class TitleActivity : AppCompatActivity() {
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
        button = findViewById(R.id.getting_started)
        button.setOnClickListener {
            val intent = Intent(this@TitleActivity, CognitiveGamesActivity::class.java)
            startActivity(intent)
        }
    }
}