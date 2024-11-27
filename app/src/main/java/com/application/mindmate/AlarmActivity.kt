package com.application.mindmate

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.application.customization.DialogActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AlarmActivity(private val activity: Activity) {

    // UI
    private lateinit var callButton: LinearLayout
    private lateinit var smsButton: LinearLayout

    // Firebase and Location
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Permission codes
    private val CALL_PERMISSION_CODE = 1
    private val LOCATION_PERMISSION_CODE = 101
    private val SMS_PERMISSION_CODE = 102

    fun useAlarm() {
        val v = DialogActivity(activity, R.layout.alarm_dialog).getDialog()

        // Initialize buttons and location client
        callButton = v.findViewById(R.id.btn_call_caregiver)
        smsButton = v.findViewById(R.id.btn_sms_loc)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        // Call button click listener
        callButton.setOnClickListener {
            fetchCaregiverDetailsAndCall()
        }

        // SMS button click listener
        smsButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
            } else {
                sendLocationSMS()
            }
        }
    }

    private fun fetchCaregiverDetailsAndCall() {
        db.collection("caregivers")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val phoneNumber = document.getString("phoneNumber")
                    if (phoneNumber != null) {
                        makePhoneCall(phoneNumber)
                    } else {
                        Toast.makeText(activity, "Caregiver phone number not found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Failed to fetch caregiver details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_CODE)
        } else {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            activity.startActivity(intent)
        }
    }

    private fun sendLocationSMS() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // perms granted, proceed with fetching location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    fetchCaregiverDetailsAndSendSMS(location)
                } else {
                    Toast.makeText(activity, "Unable to fetch location.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // perms not granted, request perms
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_CODE)
        }
    }

    private fun fetchCaregiverDetailsAndSendSMS(location: android.location.Location) {
        db.collection("caregivers")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val phoneNumber = document.getString("phoneNumber")
                    if (phoneNumber != null) {
                        composeAndSendSMS(phoneNumber, location)
                        saveLocationToFirestore(location)
                    } else {
                        Toast.makeText(activity, "Caregiver phone number not found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Failed to fetch caregiver details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun composeAndSendSMS(phoneNumber: String, location: android.location.Location) {
        val message = "EMERGENCY! Here is my current location: https://maps.google.com/?q=${location.latitude},${location.longitude}. You can check it in MindMate as well!"

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
        } else {
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", message)
            }
            activity.startActivity(smsIntent)
        }
    }

    private fun saveLocationToFirestore(location: android.location.Location) {
        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude
        )

        db.collection("users").document(userId)
            .set(locationData)
            .addOnSuccessListener {
                Toast.makeText(activity, "Location saved successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Failed to save location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}