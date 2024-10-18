package com.application.mindmate.registration

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.common.AnimationUtils
import com.application.common.InputValidation
import com.application.common.ProgressBarManagement
import com.application.mindmate.R
import com.hbb20.CountryCodePicker

@Suppress("DEPRECATION")
class CaregiverInformationActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var ccpCountryCode: CountryCodePicker
    private lateinit var btnProceed: LinearLayout
    private lateinit var progressBarRegister: ProgressBar
    private lateinit var caregiverEmail: String
    private lateinit var caregiverPhoneNumber: String
    private lateinit var textProceed: TextView
    private var emailProgressAdded = false
    private var phoneNumberProgressAdded = false
    private var progress = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_information)
        var intent = intent
        val table = intent.getStringExtra("TABLE")
        editTextEmail = findViewById(R.id.edit_text_caregiver_email)
        editTextPhoneNumber = findViewById(R.id.edit_text_caregiver_phone)
        ccpCountryCode = findViewById(R.id.ccp_country_code)
        progressBarRegister = findViewById(R.id.pb_registration)
        setupListeners()
        btnProceed = findViewById(R.id.btn_proceed)
        btnProceed.setOnClickListener {
            if (validateInput()) {
                intent = Intent(this, PersonalInformationActivity::class.java)
                intent.putExtra("CAREGIVER_EMAIL", caregiverEmail)
                intent.putExtra("CAREGIVER_PHONE", caregiverPhoneNumber)
                intent.putExtra("TABLE", table)
                intent.putExtra("PROGRESS", progress)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid email or phone number!",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        ProgressBarManagement.setupSomethingTextWatcher(editTextEmail,
            { isEmail -> updateProgressBarEmail(isEmail)}, emailProgressAdded)
        ProgressBarManagement.setupSomethingTextWatcher(editTextPhoneNumber,
            { isPhoneNumber -> updateProgressBarPhoneNumber(isPhoneNumber)}, phoneNumberProgressAdded)
    }

    private fun updateProgressBarEmail(isEmail: Boolean = false) {
        if (isEmail && !emailProgressAdded) {
            progress += 12
            emailProgressAdded = true
        } else if (!isEmail && emailProgressAdded) {
            progress -= 12
            emailProgressAdded = false
        }
        AnimationUtils.animateProgressBar(progressBarRegister, progress)
        textProceed = findViewById(R.id.text_proceed)
        if (progress == 24) textProceed.setTextColor(resources.getColor(R.color.colorPrimary))
        else textProceed.setTextColor(resources.getColor(R.color.background))
    }

    private fun updateProgressBarPhoneNumber(isPhoneNumber: Boolean = false) {
        if (isPhoneNumber && !phoneNumberProgressAdded) {
            progress += 12
            phoneNumberProgressAdded = true
        } else if (!isPhoneNumber && phoneNumberProgressAdded) {
            progress -= 12
            phoneNumberProgressAdded = false
        }
        AnimationUtils.animateProgressBar(progressBarRegister, progress)
        textProceed = findViewById(R.id.text_proceed)
        if (progress == 24) textProceed.setTextColor(resources.getColor(R.color.colorPrimary))
        else textProceed.setTextColor(resources.getColor(R.color.background))
    }

    private fun validateEmail(email: String): Boolean {
        return InputValidation.isEmailValid(email)
    }
    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return InputValidation.isPhoneNumberValid(phoneNumber)
    }
    private fun validateInput(): Boolean {
        caregiverEmail = editTextEmail.text.toString().trim{ it <= ' '}
        caregiverPhoneNumber = editTextPhoneNumber.text.toString().trim{ it <= ' '}
        val isEmailValid = validateEmail(caregiverEmail)
        val isPhoneNumberValid = validatePhoneNumber(caregiverPhoneNumber)
        return isEmailValid && isPhoneNumberValid
    }
}