package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {
    private var email: EditText? = null
    private var password: EditText? = null
    private lateinit var loginButton: Button
    private lateinit var signUp: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signUp = findViewById(R.id.sign_up)
        signUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        email = findViewById(R.id.login_email)
        password = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        if (validateLoginDetails()) {
            val email = email?.text.toString().trim {it <= ' '}
            val password = password?.text.toString().trim {it <= ' '}
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar(resources.getString(R.string.login_success), false)
                        goToDashboard()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private fun goToDashboard() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.email.toString()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
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