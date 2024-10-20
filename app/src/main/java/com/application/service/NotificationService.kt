package com.application.service

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class NotificationService(private val context: Context) {

    companion object {
        const val REQUEST_SEND_SMS_PERMISSION = 123
    }

    fun sendSMS(phoneNumber: String, message: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_SEND_SMS_PERMISSION
            )
        } else {
            sendTextMessage(phoneNumber, message)
        }
    }

    private fun sendTextMessage(phoneNumber: String, message: String) {
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java).createForSubscriptionId(1)
            } else {
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "Message sent to a caregiver!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error sending a message!", Toast.LENGTH_SHORT).show()
            Log.e("NotificationService", "Error: ${e.message}", e)
        }
    }
}