package com.application.mindmate.both

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.mindmate.R
import com.application.mindmate.registration.CaregiverInformationActivity
import com.application.mindmate.registration.PersonalInformationActivity
import com.application.mindmate.registration.SignUpInformationActivity

class ChooseRoleActivity : AppCompatActivity()  {
    private lateinit var btnImPatient: LinearLayout
    private lateinit var btnImCaregiver: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_role)
        btnImPatient = findViewById(R.id.btn_im_patient)
        btnImCaregiver = findViewById(R.id.btn_im_caregiver)
        btnImPatient.setOnClickListener {
            val intent = Intent(this, CaregiverInformationActivity::class.java)
            intent.putExtra("TABLE", "patients")
            startActivity(intent)
        }
        btnImCaregiver.setOnClickListener {
            val intent = Intent(this, PersonalInformationActivity::class.java)
            intent.putExtra("TABLE", "caregivers")
            startActivity(intent)
        }
        ActivityUtils.changeActivity<LinearLayout>(R.id.btn_im_already_signed, this, LoginActivity())
    }
}