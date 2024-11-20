package com.application.mindmate.games

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.enums.UserRole
import com.application.mindmate.R


class CognitiveGamesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cognitive_games)
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        ActivityUtils.changeActivity<ImageView>(R.id.go_to_craft_rhyme, this, CraftRhymeActivity())
        ActivityUtils.changeActivity<ImageView>(R.id.go_to_guess_word, this, GuessWordChooseCategoryActivity())
    }
}