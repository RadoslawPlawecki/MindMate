package com.application.mindmate.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.common.AnimationUtils
import com.application.common.InputValidation
import com.application.common.ProgressBarManagement
import com.application.customization.BaseActivity
import com.application.mindmate.R
import java.util.Calendar
import kotlin.math.exp

@Suppress("DEPRECATION")
class PersonalInformationActivity : BaseActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextDob: EditText
    private lateinit var btnMale: LinearLayout
    private lateinit var btnFemale: LinearLayout
    private lateinit var btnGoToSignUpInformation: LinearLayout
    private lateinit var progressBarRegister: ProgressBar
    private lateinit var userName: String
    private lateinit var dateOfBirth: String
    private lateinit var userGender: String
    private lateinit var textProceed: TextView
    private var nameProgressAdded = false
    private var dobProgressAdded = false
    private var genderProgressAdded = false
    private var progress = 0
    private var role = "patients"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)
        var intent = intent
        val table = intent.getStringExtra("TABLE")
        val caregiverEmail = intent.getStringExtra("CAREGIVER_EMAIL")
        val caregiverPhone = intent.getStringExtra("CAREGIVER_PHONE")
        progressBarRegister = findViewById(R.id.pb_registration)
        if (table == "caregivers") {
            role = "caregivers"
            progress = 24
            progressBarRegister.progress = progress
        }
        btnMale = findViewById(R.id.btn_male)
        btnFemale = findViewById(R.id.btn_female)
        btnGoToSignUpInformation = findViewById(R.id.btn_go_to_sign_in_information)
        editTextName = findViewById(R.id.edit_text_name)
        editTextDob = findViewById(R.id.edit_text_dob)
        textProceed = findViewById(R.id.text_proceed)
        setupListeners()
        val expectedValue = if (role == "caregivers") 60 else 48
        btnGoToSignUpInformation.setOnClickListener {
            if (progress >= expectedValue) {
                if (validateInput()) {
                    intent = Intent(this, SignUpInformationActivity::class.java)
                    intent.putExtra("NAME", userName)
                    intent.putExtra("DOB", dateOfBirth)
                    intent.putExtra("GENDER", userGender)
                    intent.putExtra("CAREGIVER_EMAIL", caregiverEmail)
                    if (table == "caregivers") {
                        intent.putExtra("CAREGIVER_PHONE", caregiverPhone)
                    }
                    intent.putExtra("TABLE", table)
                    startActivity(intent)
                } else {
                    showErrorSnackBar("Invalid name or date of birth!", true)
                }
            } else {
                showErrorSnackBar("Complete the data!", true)
            }
        }
    }

    private fun setupListeners() {
        ProgressBarManagement.setupSomethingTextWatcher(editTextName,
            { isName -> updateProgressBarName(isName)}, nameProgressAdded)
        editTextDob.setOnClickListener {
            showDatePickerDialog()
            Toast.makeText(this, "Select your birth date!",
                Toast.LENGTH_LONG).show()
        }
        btnMale.setOnClickListener {
            selectGender(btnMale, btnFemale)
            userGender = "Male"
        }
        btnFemale.setOnClickListener {
            selectGender(btnFemale, btnMale)
            userGender = "Female"
        }
    }

    private fun selectGender(selected: LinearLayout, other: LinearLayout) {
        selected.setBackgroundResource(R.drawable.bg_rounded_corner_10_framed)
        other.setBackgroundResource(R.drawable.bg_rounded_corner_10)
        updateProgressBarGender()
    }

    private fun updateProgressBarName(isName: Boolean = false) {
        if (isName && !nameProgressAdded) {
            progress += if (role == "caregivers") 12
            else 16
            nameProgressAdded = true
        } else if (!isName && nameProgressAdded) {
            progress -= if (role == "caregivers") 12
            else 16
            nameProgressAdded = false
        }
        updateProgressBarVisuals()
    }

    private fun updateProgressBarDob() {
        if (!dobProgressAdded) {
            progress += if (role == "caregivers") 12
            else 16
            dobProgressAdded = true
        }
        updateProgressBarVisuals()
    }

    private fun updateProgressBarGender() {
        if (!genderProgressAdded) {
            progress += if (role == "caregivers") 12
            else 16
            genderProgressAdded = true
        }
        updateProgressBarVisuals()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this, R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editTextDob.setText(dob)
                updateProgressBarDob()
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun updateProgressBarVisuals() {
        val expectedValue = if (role == "caregivers") 60 else 48
        AnimationUtils.animateProgressBar(progressBarRegister, progress)
        if (progress >= expectedValue) textProceed.setTextColor(resources.getColor(R.color.colorPrimary))
        else textProceed.setTextColor(resources.getColor(R.color.background))
    }

    private fun validateName(name: String): Boolean {
        return InputValidation.isNameValid(name)
    }
    private fun validateDateOfBirth(date: String): Boolean {
        return InputValidation.isDateOfBirthValid(date)
    }
    private fun validateInput(): Boolean {
        userName = editTextName.text.toString().trim { it <= ' ' }
        dateOfBirth = editTextDob.text.toString().trim{ it <= ' ' }
        return validateName(userName) && validateDateOfBirth(dateOfBirth)
    }
}