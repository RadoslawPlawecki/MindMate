package com.application.mindmate.both

import android.annotation.SuppressLint
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
import com.application.mindmate.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterActivity : BaseActivity() {
    private var email: EditText? = null
    private var name: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private lateinit var registerButton: Button
    private lateinit var signIn: TextView
    private lateinit var db: FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        signIn = findViewById(R.id.sign_in)
        // if an user has already an account, go to the login activity
        signIn.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        email = findViewById(R.id.register_email)
        name = findViewById(R.id.register_name)
        password = findViewById(R.id.register_password)
        confirmPassword = findViewById(R.id.register_confirm_password)
        registerButton = findViewById(R.id.register_button)
        db = FirebaseFirestore.getInstance()
        // if an user filled out the form, try to register them
        registerButton.setOnClickListener {
            lifecycleScope.launch {
                registerUser()
            }
        }
    }
    private fun validateRegisterDetails(): Boolean {
        val nameRegex = Regex("^[a-zA-Z]+$")
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$")
        return when {
            TextUtils.isEmpty(name?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_name), true)
                false
            }
            !nameRegex.matches(name?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_name_has_numbers), true)
                false
            }
            TextUtils.isEmpty(email?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_mail), true)
                false
            }
            TextUtils.isEmpty(password?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_password), true)
                false
            }
            TextUtils.isEmpty(confirmPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_confirm_password), true)
                false
            }
            password?.text.toString().trim {it <= ' '} != confirmPassword?.text.toString().trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_not_the_same_password), true)
                false
            }
            !passwordRegex.matches(password?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.error_msg_password_not_strong), true)
                false
            }
            else -> true
        }
    }

    private suspend fun registerUser() {
        if (validateRegisterDetails()) {
            val login = email?.text.toString().trim { it <= ' ' }
            val password = password?.text.toString().trim { it <= ' ' }
            val name = name?.text.toString().trim { it <= ' '}

            try {
                val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password).await()
                val firebaseUser = authResult.user!!
                Log.d("RegisterActivity", "User registered: ${firebaseUser.uid}")
                checkIfAdminExists(firebaseUser, login, name)
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Registration error: ${e.message}")
                showErrorSnackBar(e.message.toString(), true)
            }
        }
    }

    private suspend fun checkIfAdminExists(firebaseUser: FirebaseUser, login: String, name: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                val role = UserRole.USER
                val userData = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "email" to login,
                    "name" to name,
                    "role" to role
                )
                db.collection("users").document(firebaseUser.uid).set(userData).await()
                Log.d("RegisterActivity", "Document added: ${firebaseUser.uid}")
                showErrorSnackBar(
                    "You are registered successfully, your user id is ${firebaseUser.uid}.",
                    false
                )
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error adding document: ${e.message}")
                showErrorSnackBar("Error adding document: ${e.message}", true)
            }
        }
    }
}