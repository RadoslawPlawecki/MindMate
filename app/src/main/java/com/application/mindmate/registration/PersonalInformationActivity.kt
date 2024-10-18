package com.application.mindmate.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.mindmate.R
import java.util.Calendar

class PersonalInformationActivity : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextDob: EditText
    private lateinit var btnMale: LinearLayout
    private lateinit var btnFemale: LinearLayout
    private lateinit var btnGoToSignUpInformation: LinearLayout
    private lateinit var progressBarRegister: ProgressBar

    private lateinit var userGender: String
    private var isNameFilled = false
    private var isDobFilled = false
    private var isGenderSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)
        var intent = intent
        val progress = intent.getStringExtra("TABLE")

        btnMale = findViewById(R.id.btn_male)
        btnFemale = findViewById(R.id.btn_female)
        btnGoToSignUpInformation = findViewById(R.id.btn_go_to_sign_in_information)
        editTextName = findViewById(R.id.edit_text_name)
        editTextDob = findViewById(R.id.edit_text_dob)
        progressBarRegister = findViewById(R.id.pb_registration)
        setupListeners()
        val table = intent.getStringExtra("TABLE")
        btnGoToSignUpInformation.setOnClickListener {
            intent = Intent(this, SignUpInformationActivity::class.java)
            intent.putExtra("NAME", editTextName.text.toString())
            intent.putExtra("DOB", editTextDob.text.toString())
            intent.putExtra("GENDER", userGender)
            intent.putExtra("TABLE", table)
            startActivity(intent)
        }
    }

    private fun setupListeners() {
        editTextName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isNameFilled = !s.isNullOrEmpty()
                updateProgressBar()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextDob.setOnClickListener {
            showDatePickerDialog()
            Toast.makeText(this, "Select your birth date",
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
                isDobFilled = true
                updateProgressBar()
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun selectGender(selected: LinearLayout, other: LinearLayout) {
        selected.setBackgroundResource(R.drawable.bg_rounded_corner_10_framed)
        other.setBackgroundResource(R.drawable.bg_rounded_corner_10)
        isGenderSelected = true
        updateProgressBar()
    }

    private fun updateProgressBar() {
        var progress = 0
        if (isNameFilled) progress += 33
        if (isDobFilled) progress += 33
        if (isGenderSelected) progress += 34
        progressBarRegister.progress = progress
    }
}