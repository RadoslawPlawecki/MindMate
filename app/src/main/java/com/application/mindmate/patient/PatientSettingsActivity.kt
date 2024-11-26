package com.application.mindmate.patient

import PatientsService
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.customization.DialogActivity
import com.application.enums.UserRole
import com.application.mindmate.R

class PatientSettingsActivity : AppCompatActivity() {
    private lateinit var changeEmailLinearLayout: LinearLayout
    private lateinit var changePasswordLinearLayout: LinearLayout
    private lateinit var changeCaregiverLinearLayout: LinearLayout
    private lateinit var patientsService: PatientsService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_patient)
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        patientsService = PatientsService()
        changeEmailLinearLayout = findViewById(R.id.btn_change_email)
        changePasswordLinearLayout = findViewById(R.id.btn_change_password)
        changeCaregiverLinearLayout = findViewById(R.id.btn_change_caregiver)
        changeEmailLinearLayout.setOnClickListener {
            changeEmailDialog()
        }
        changePasswordLinearLayout.setOnClickListener {
            changePasswordDialog()
        }
        changeCaregiverLinearLayout.setOnClickListener {
            changeCaregiverDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeEmailDialog() {
        val v = DialogActivity(this, R.layout.dg_change).getDialog()
        val titleTextView = v.findViewById<TextView>(R.id.change_in_text_view)
        titleTextView.text = "CHANGE EMAIL"
        val toChangeEditText = v.findViewById<EditText>(R.id.to_change_edit_text)
        toChangeEditText.hint = "New email address"
        toChangeEditText.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
        val confirmButton = v.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            Toast.makeText(this@PatientSettingsActivity,
                "Here the mail can be changed!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changePasswordDialog() {
        val v = DialogActivity(this, R.layout.dg_change).getDialog()
        val titleTextView = v.findViewById<TextView>(R.id.change_in_text_view)
        titleTextView.text = "CHANGE PASSWORD"
        val toChangeEditText = v.findViewById<EditText>(R.id.to_change_edit_text)
        toChangeEditText.hint = "New password"
        toChangeEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        val confirmButton = v.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            Toast.makeText(this@PatientSettingsActivity,
                "Here the password can be changed!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeCaregiverDialog() {
        val v = DialogActivity(this, R.layout.dg_change).getDialog()
        val titleTextView = v.findViewById<TextView>(R.id.change_in_text_view)
        titleTextView.text = "CHANGE CAREGIVER"
        val toChangeEditText = v.findViewById<EditText>(R.id.to_change_edit_text)
        toChangeEditText.hint = "New caregiver's phone number"
        toChangeEditText.inputType = InputType.TYPE_CLASS_PHONE
        val confirmButton = v.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            Toast.makeText(this@PatientSettingsActivity,
                "Here the caregiver can be changed!", Toast.LENGTH_SHORT).show()
        }
    }
}