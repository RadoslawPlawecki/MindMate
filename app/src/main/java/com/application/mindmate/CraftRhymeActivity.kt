package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CraftRhymeActivity : AppCompatActivity() {
    private lateinit var openMenu: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_craft_rhyme)
        openMenu = findViewById(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(this@CraftRhymeActivity, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}