package com.application.mindmate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.LinearLayout
import android.widget.Toast
import com.application.customization.DialogActivity

class AlarmActivity(private val activity: Activity) {
    private lateinit var callButton: LinearLayout
    private lateinit var phoneNumber: String
    fun useAlarm() {
        val v = DialogActivity(activity, R.layout.dg_alarm).getDialog()
        phoneNumber = "606670149"
        callButton = v.findViewById(R.id.btn_call_caregiver)
        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            if (phoneNumber != "606670149") {
                Toast.makeText(activity, "It's not possible to call this number!", Toast.LENGTH_SHORT).show()
            } else {
                intent.data = Uri.parse("tel:" + phoneNumber)
                activity.startActivity(intent)
            }
        }
    }
}