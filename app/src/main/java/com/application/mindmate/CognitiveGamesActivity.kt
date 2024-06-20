package com.application.mindmate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils


class CognitiveGamesActivity : AppCompatActivity() {
    private lateinit var goToCraftRhymeActivity: ImageView
    private lateinit var goToGuessWordActivity: ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cognitive_games)
        ActivityUtils.actionBarSetup(this)
        goToCraftRhymeActivity = findViewById(R.id.go_to_craft_rhyme)
        goToCraftRhymeActivity.setOnClickListener {
            val intent = Intent(this@CognitiveGamesActivity, CraftRhymeActivity::class.java)
            startActivity(intent)
        }
        goToGuessWordActivity = findViewById(R.id.go_to_guess_word)
        goToGuessWordActivity.setOnClickListener {
            val intent = Intent(this@CognitiveGamesActivity, GuessWordActivity::class.java)
            startActivity(intent)
        }
    }
}