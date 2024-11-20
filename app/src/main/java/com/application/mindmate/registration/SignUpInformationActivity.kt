package com.application.mindmate.registration

import PatientsService
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.application.common.AnimationUtils
import com.application.common.InputValidation
import com.application.common.ProgressBarManagement
import com.application.customization.BaseActivity
import com.application.mindmate.R
import com.application.mindmate.both.LoginActivity
import com.application.mindmate.caregiver.CaregiverDashboardActivity
import com.application.mindmate.patient.PatientDashboardActivity
import com.application.service.CaregiversService
import com.application.service.NotificationService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
class SignUpInformationActivity : BaseActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRepeatPassword: EditText
    private lateinit var checkBoxTermsAndConditions: CheckBox
    private lateinit var textTermsAndConditions: TextView
    private lateinit var btnSignUp: Button
    private lateinit var progressBarRegister: ProgressBar
    private lateinit var textProceed: TextView
    private lateinit var userEmail: String
    private lateinit var password: String
    private lateinit var repeatPassword: String
    private lateinit var patientsService: PatientsService
    private lateinit var caregiversService: CaregiversService
    private var emailProgressAdded = false
    private var passwordProgressAdded = false
    private var repeatPasswordProgressAdded = false
    private var checkBoxProgressAdded = false
    private var progress = 48
    private var role = "patients"
    private var table = ""
    private var name = ""
    private var dateOfBirth = ""
    private var gender = ""
    private var caregiverEmail = ""
    private var caregiverPhone = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_information)
        // initialize services
        patientsService = PatientsService()
        caregiversService = CaregiversService()
        textTermsAndConditions = findViewById(R.id.text_terms_conditions)
        textTermsAndConditions.text = Html.fromHtml(getString(R.string.agree_with_terms_amp_conditions))
        textTermsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        progressBarRegister = findViewById(R.id.pb_registration)
        val intent = intent
        table = intent.getStringExtra("TABLE").toString()
        name = intent.getStringExtra("NAME").toString()
        dateOfBirth = intent.getStringExtra("DOB").toString()
        gender = intent.getStringExtra("GENDER").toString()
        caregiverEmail = intent.getStringExtra("CAREGIVER_EMAIL").toString()
        caregiverPhone = intent.getStringExtra("CAREGIVER_PHONE").toString()
        if (table == "caregivers") {
            role = "caregivers"
            progress = 60
        }
        progressBarRegister.progress = progress
        editTextEmail = findViewById(R.id.edit_text_email)
        editTextPassword = findViewById(R.id.edit_text_password)
        editTextRepeatPassword = findViewById(R.id.edit_text_repeat_password)
        checkBoxTermsAndConditions = findViewById(R.id.check_box_terms)
        textProceed = findViewById(R.id.text_proceed)
        setupListeners()
        btnSignUp = findViewById(R.id.btn_sign_up)
        btnSignUp.setOnClickListener {
            if (validateInput()) {
                lifecycleScope.launch {
                    signUpUser()
                }
            } else {
                showErrorSnackBar("Invalid email or password!", true)
            }
        }
    }

    private suspend fun signUpUser() {
        val patientsService = PatientsService()
        try {
            if (table == "patients" && caregiverEmail.isNotEmpty()) {
                val caregiverExists = patientsService.caregiverExists(caregiverEmail)
                if (!caregiverExists) {
                    Log.e("SignUpInformationActivity",
                        "Caregiver account not found. Registration aborted.")
                    patientsService.notifyCaregiverToSetAccount(this,
                        caregiverEmail = caregiverEmail, name = name)
                    return
                }
            }
            val authResult =
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, password)
                    .await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("User creation failed")
            Log.d("SignUpInformationActivity", "User registered: ${firebaseUser.uid}")
            if (table == "patients") {
                patientsService.addPatient(firebaseUser, name, dateOfBirth, caregiverEmail, gender,
                    userEmail)
                val intent = Intent(this@SignUpInformationActivity, LoginActivity::class.java)
                startActivity(intent)
                showErrorSnackBar("You were signed up successfully!", false)
            } else {
                caregiversService.addCaregiver(firebaseUser, name, dateOfBirth, userEmail, gender, caregiverPhone)
                val intent = Intent(this@SignUpInformationActivity, LoginActivity::class.java)
                startActivity(intent)
                showErrorSnackBar("You were signed up successfully!", false)
            }
        } catch (e: Exception) {
            Log.e("SignUpInformationActivity", "Error during registration: ${e.message}", e)
            Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        ProgressBarManagement.setupSomethingTextWatcher(editTextEmail,
            { isEmail -> updateProgressBarEmail(isEmail)}, emailProgressAdded)
        ProgressBarManagement.setupSomethingTextWatcher(editTextPassword,
            { isPassword -> updateProgressBarPassword(isPassword)}, passwordProgressAdded)
        ProgressBarManagement.setupSomethingTextWatcher(editTextRepeatPassword,
            { isRepeatPassword -> updateProgressBarRepeatPassword(isRepeatPassword)}, repeatPasswordProgressAdded)
        checkBoxTermsAndConditions.setOnClickListener {
            updateProgressBarCheckBox()
        }
    }

    private fun updateProgressBarEmail(isEmail: Boolean = false) {
        if (isEmail && !emailProgressAdded) {
            progress += if (role == "patients") 16
            else 12
            emailProgressAdded = true
        } else if (!isEmail && emailProgressAdded) {
            progress -= if (role == "patients") 16
            else 12
            emailProgressAdded = false
        }
        updateProgressBarVisuals()
    }

    private fun updateProgressBarPassword(isPassword: Boolean = false) {
        if (isPassword && !passwordProgressAdded) {
            progress += if (role == "patients") 16
            else 12
            passwordProgressAdded = true
        } else if (!isPassword && passwordProgressAdded) {
            progress -= if (role == "patients") 16
            else 12
            passwordProgressAdded = false
        }
        updateProgressBarVisuals()
    }

    private fun updateProgressBarRepeatPassword(isRepeatPassword: Boolean = false) {
        if (isRepeatPassword && !repeatPasswordProgressAdded) {
            progress += if (role == "patients") 16
            else 12
            repeatPasswordProgressAdded = true
        } else if (!isRepeatPassword && repeatPasswordProgressAdded) {
            progress -= if (role == "patients") 16
            else 12
            repeatPasswordProgressAdded = false
        }
        updateProgressBarVisuals()
    }

    private fun updateProgressBarCheckBox() {
        if (!checkBoxProgressAdded) {
            progress += 4
            checkBoxProgressAdded = true
        } else if (checkBoxProgressAdded) {
            progress -= 4
            checkBoxProgressAdded = false
        }
        updateProgressBarVisuals()
    }

    private fun updateProgressBarVisuals() {
        val expectedValue = 100
        AnimationUtils.animateProgressBar(progressBarRegister, progress)
        if (progress >= expectedValue) textProceed.setTextColor(resources.getColor(R.color.colorPrimary))
        else textProceed.setTextColor(resources.getColor(R.color.background))
    }
    private fun validateEmail(email: String): Boolean {
        return InputValidation.isEmailValid(email)
    }
    private fun validatePassword(password: String): Boolean {
        return InputValidation.isPasswordValid(password)
    }
    private fun validatePasswordMatch(password: String, repeatPassword: String): Boolean {
        return InputValidation.isTextMatching(password, repeatPassword)
    }
    private fun validateInput(): Boolean {
        userEmail = editTextEmail.text.toString().trim{ it <= ' '}
        password = editTextPassword.text.toString().trim{ it <= ' '}
        repeatPassword = editTextRepeatPassword.text.toString().trim{ it <= ' '}
        val isEmailValid = validateEmail(userEmail)
        val isPasswordValid = validatePassword(password)
        val isRepeatPasswordValid = validatePassword(repeatPassword)
        val isPasswordMatching = validatePasswordMatch(password, repeatPassword)
        return isEmailValid && isPasswordValid && isRepeatPasswordValid && isPasswordMatching && checkBoxProgressAdded
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NotificationService.REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showErrorSnackBar("SMS permission granted! Resending a message.", false)
            } else {
                showErrorSnackBar("SMS permission denied! Cannot send a message.", true)
            }
        }
    }
}