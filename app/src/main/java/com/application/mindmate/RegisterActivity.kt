package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {
    private var email: EditText? = null
    private var name: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private lateinit var registerButton: Button
    private lateinit var signIn: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        signIn = findViewById(R.id.sign_in)
        signIn.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        email = findViewById(R.id.register_mail)
        name = findViewById(R.id.register_name)
        password = findViewById(R.id.register_password)
        confirmPassword = findViewById(R.id.register_confirm_password)
        registerButton = findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            registerUser()
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

    private fun registerUser() {
        if (validateRegisterDetails()) {
            // remove whitespaces
            val login: String = email?.text.toString().trim {it <= ' '}
            val password: String = password?.text.toString().trim {it <= ' '}
            // create a user in FirebaseAuth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            showErrorSnackBar("You are registered successfully, your user id is ${firebaseUser.uid}.", false)
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this@RegisterActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
                )
        }
    }
}