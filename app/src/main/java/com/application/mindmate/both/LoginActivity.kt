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
import com.application.enums.UserRole
import com.application.mindmate.DashboardActivity
import com.application.mindmate.R
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
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
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
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            try {
                val document = userRef.get().await()
                Log.d("LoginActivity", "Document: $document")
                if (document != null && document.exists()) {
                    val role = document.getString("role")
                    when (role) {
                        UserRole.ADMIN.toString() -> {
                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            startActivity(intent)
                        }
                        UserRole.USER.toString() -> {
                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            startActivity(intent)
                        }
                        else -> {
                            showErrorSnackBar("Role not found!", true)
                        }
                    }
                } else {
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