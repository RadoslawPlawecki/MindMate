package com.application.mindmate.caregiver

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

class CaregiverSettingsActivity : AppCompatActivity() {
    private lateinit var changeEmailLinearLayout: LinearLayout
    private lateinit var changePasswordLinearLayout: LinearLayout
    private lateinit var changePhoneNumberLinearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_caregiver)
        ActivityUtils.actionBarSetup(this, UserRole.CAREGIVER)
        changeEmailLinearLayout = findViewById(R.id.btn_change_email)
        changePasswordLinearLayout = findViewById(R.id.btn_change_password)
        changePhoneNumberLinearLayout = findViewById(R.id.btn_change_phone_number)
        changeEmailLinearLayout.setOnClickListener {
            changeEmailDialog()
        }
        changePasswordLinearLayout.setOnClickListener {
            changePasswordDialog()
        }
        changePhoneNumberLinearLayout.setOnClickListener {
            changePhoneNumberDialog()
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
            Toast.makeText(this@CaregiverSettingsActivity,
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
            Toast.makeText(this@CaregiverSettingsActivity,
                "Here the password can be changed!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changePhoneNumberDialog() {
        val v = DialogActivity(this, R.layout.dg_change).getDialog()
        val titleTextView = v.findViewById<TextView>(R.id.change_in_text_view)
        titleTextView.text = "CHANGE PHONE NUMBER"
        val toChangeEditText = v.findViewById<EditText>(R.id.to_change_edit_text)
        toChangeEditText.hint = "New phone number"
        toChangeEditText.inputType = InputType.TYPE_CLASS_PHONE
        val confirmButton = v.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            Toast.makeText(this@CaregiverSettingsActivity,
                "Here the phone number can be changed!", Toast.LENGTH_SHORT).show()
        }
    }
}