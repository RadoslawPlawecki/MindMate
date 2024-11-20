package com.application.mindmate.registration;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText
import com.application.common.InputValidation
import com.application.customization.DialogActivity
import com.application.mindmate.R
import java.lang.IllegalArgumentException

class InviteCaregiverActivity(private val activity: Activity) {
    private lateinit var editTextCaregiverEmail: EditText
    private lateinit var btnInviteCaregiver: Button

    fun inviteCaregiver(onEmailEntered: (String) -> Unit, onError: (String) -> Unit) {
        val v = DialogActivity(activity, R.layout.dg_invite_caregiver).getDialog()
        editTextCaregiverEmail = v.findViewById(R.id.edit_text_caregiver_email)
        btnInviteCaregiver = v.findViewById(R.id.btn_invite_caregiver)

        btnInviteCaregiver.setOnClickListener {
            val caregiverEmail = editTextCaregiverEmail.text.trim().toString()
            if (validateEmail(caregiverEmail)) {
                onEmailEntered(caregiverEmail)
            } else {
                onError("The email is incorrect! Please enter a valid email.")
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return InputValidation.isEmailValid(email)
    }
}
