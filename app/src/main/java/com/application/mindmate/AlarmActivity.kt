package com.application.mindmate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.application.customization.DialogActivity

class AlarmActivity(private val activity: Activity) {
    private lateinit var callButton: Button
    private lateinit var phoneNumber: TextView
    fun useAlarm() {
        val v = DialogActivity(activity, R.layout.alarm_dialog).getDialog()
        phoneNumber = v.findViewById(R.id.phone_number_edit_text)
        callButton = v.findViewById(R.id.call_button)
        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            if (phoneNumber.text != "606670149") {
                Toast.makeText(activity, "It's not possible to call this number!", Toast.LENGTH_SHORT).show()
            } else {
                intent.data = Uri.parse("tel:" + phoneNumber.text.toString())
                activity.startActivity(intent)
            }
        }
    }
}