package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class CognitiveGamesActivity : AppCompatActivity() {
    private lateinit var openMenu: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cognitive_games)
        openMenu = findViewById(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(this@CognitiveGamesActivity, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}