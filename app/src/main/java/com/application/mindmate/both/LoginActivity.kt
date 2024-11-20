package com.application.mindmate.both

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.application.customization.BaseActivity
import com.application.mindmate.R
import com.application.mindmate.caregiver.CaregiverDashboardActivity
import com.application.mindmate.patient.PatientDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : BaseActivity() {
    private var email: EditText? = null
    private var password: EditText? = null
    private lateinit var loginButton: Button
    private lateinit var signUp: TextView
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // if an user hasn't had an account, go to the sign up activity
        signUp = findViewById(R.id.sign_up)
        signUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, TitleActivity::class.java)
            startActivity(intent)
        }
        email = findViewById(R.id.login_email)
        password = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        db = FirebaseFirestore.getInstance()
        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        if (validateLoginDetails()) {
            val email = email?.text.toString().trim { it <= ' ' }
            val password = password?.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lifecycleScope.launch {
                            checkUserRole()
                        }
                        showErrorSnackBar(resources.getString(R.string.login_success), false)
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private suspend fun checkUserRole() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            Log.d("LoginActivity", "User ID: $userId")
            val patientRef = FirebaseFirestore.getInstance().collection("patients").document(userId)
            val caregiverRef = FirebaseFirestore.getInstance().collection("caregivers").document(userId)
            try {
                val patientDocument = patientRef.get().await()
                val caregiverDocument = caregiverRef.get().await()
                if (patientDocument != null && patientDocument.exists()) {
                    val intent = Intent(this@LoginActivity, PatientDashboardActivity::class.java)
                    startActivity(intent)
                } else if (caregiverDocument != null && caregiverDocument.exists()) {
                    val intent = Intent(this@LoginActivity, CaregiverDashboardActivity::class.java)
                    startActivity(intent)
                }
                else {
                    showErrorSnackBar("Document not found!", true)
                }
            } catch (e: Exception) {
                showErrorSnackBar(e.message.toString(), true)
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(email?.text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_mail), true)
                false
            }
            TextUtils.isEmpty(password?.text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid!", false)
                true
            }
        }
    }
}