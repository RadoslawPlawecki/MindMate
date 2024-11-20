package com.application.mindmate.both

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.application.common.ActivityUtils
import com.application.customization.BaseActivity
import com.application.mindmate.R
import com.application.mindmate.registration.CaregiverInformationActivity
import com.application.mindmate.registration.InviteCaregiverActivity
import com.application.mindmate.registration.PersonalInformationActivity

class ChooseRoleActivity : BaseActivity()  {
    private lateinit var btnImPatient: LinearLayout
    private lateinit var btnImCaregiver: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_role)
        btnImPatient = findViewById(R.id.btn_im_patient)
        btnImCaregiver = findViewById(R.id.btn_im_caregiver)
        btnImPatient.setOnClickListener {
            var caregiverEmail: String?
            InviteCaregiverActivity(this).inviteCaregiver(
                onEmailEntered = { email ->
                    caregiverEmail = email
                    val intent = Intent(this, PersonalInformationActivity::class.java)
                    intent.putExtra("TABLE", "patients")
                    intent.putExtra("CAREGIVER_EMAIL", caregiverEmail)
                    startActivity(intent)
                },
                onError = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
        btnImCaregiver.setOnClickListener {
            val intent = Intent(this, CaregiverInformationActivity::class.java)
            intent.putExtra("TABLE", "caregivers")
            startActivity(intent)
        }
        ActivityUtils.changeActivity<LinearLayout>(R.id.btn_im_already_signed, this, LoginActivity())
    }
}