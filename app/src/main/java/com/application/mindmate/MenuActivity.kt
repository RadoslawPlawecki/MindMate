package com.application.mindmate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var goToHomeImage: ImageView
    private lateinit var goToHomeText: TextView
    private lateinit var signOutImage: ImageView
    private lateinit var signOutText: TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        goToHomeImage = findViewById(R.id.image_home)
        goToHomeText = findViewById(R.id.text_home)
        goToHomeImage.setOnClickListener(goToHome())
        goToHomeText.setOnClickListener(goToHome())
        signOutImage = findViewById(R.id.image_sign_out)
        signOutText = findViewById(R.id.text_sign_out)
        signOutImage.setOnClickListener(signOut())
        signOutText.setOnClickListener(signOut())
    }

    private fun goToHome(): View.OnClickListener {
        val intent = Intent(this@MenuActivity, DashboardActivity::class.java)
        val clickListener = View.OnClickListener {
            startActivity(intent) }
        return clickListener
    }

    private fun signOut(): View.OnClickListener {
        val intent = Intent(this@MenuActivity, LoginActivity::class.java)
        val clickListener = View.OnClickListener {
            startActivity(intent)
        }
        return clickListener
    }
}