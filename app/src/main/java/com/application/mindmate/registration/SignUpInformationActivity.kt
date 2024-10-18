package com.application.mindmate.registration

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.mindmate.R

@Suppress("DEPRECATION")
class SignUpInformationActivity : AppCompatActivity() {
    private lateinit var textTermsAndConditions: TextView
    private lateinit var btnSignUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_information)
        textTermsAndConditions = findViewById(R.id.text_terms_conditions)
        textTermsAndConditions.text = Html.fromHtml(getString(R.string.agree_with_terms_amp_conditions))
        textTermsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        btnSignUp = findViewById(R.id.btn_sign_up)
        val intent = intent
        val name = intent.getStringExtra("NAME")
        val dob = intent.getStringExtra("DOB")
        val gender = intent.getStringExtra("TABLE")
        btnSignUp.setOnClickListener {
            Toast.makeText(this, "Name: $name, DOB: $dob, Gender: $gender", Toast.LENGTH_LONG).show()
        }
    }
}