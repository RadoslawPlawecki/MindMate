package com.application.mindmate

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.telephony.SmsManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.application.customization.DialogActivity
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlarmActivity(private val activity: Activity) {

    private lateinit var callButton: LinearLayout
    private lateinit var smsLocButton: LinearLayout
    private var phoneNumber: String? = null
    private val db = FirebaseFirestore.getInstance()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null

    companion object {
        const val REQUEST_CALL_PERMISSION = 1
        const val REQUEST_LOCATION_PERMISSION = 2
        const val REQUEST_SMS_PERMISSION = 3
    }

    fun useAlarm() {
        val v = DialogActivity(activity, R.layout.dg_alarm).getDialog()
        callButton = v.findViewById(R.id.btn_call_caregiver)
        smsLocButton = v.findViewById(R.id.btn_sms_loc)

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        // Fetch the caregiver's phone number
        fetchCaregiverPhoneNumber()

        callButton.setOnClickListener {
            if (phoneNumber.isNullOrEmpty()) {
                Toast.makeText(activity, "Can't fetch caregiver's phone number!", Toast.LENGTH_SHORT).show()
            } else {
                // Check for CALL_PHONE permission
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
                } else {
                    // Permission granted, make the call
                    makePhoneCall(phoneNumber!!)
                }
            }
        }

        smsLocButton.setOnClickListener {
            if (phoneNumber.isNullOrEmpty()) {
                Toast.makeText(activity, "Can't fetch caregiver's phone number!", Toast.LENGTH_SHORT).show()
            } else {
                // Check for location permission
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request location permission
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
                } else {
                    // Permission granted, get location and send SMS
                    getLocationAndSendSMS()
                }
            }
        }
    }

    private fun fetchCaregiverPhoneNumber() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val patientUid = currentUser.uid
            // Get patient data from Firestore
            db.collection("patients").document(patientUid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val caregiverId = documentSnapshot.getString("caregiverId")
                        if (!caregiverId.isNullOrEmpty()) {
                            // Now get caregiver's phone number
                            db.collection("caregivers").document(caregiverId).get()
                                .addOnSuccessListener { caregiverSnapshot ->
                                    if (caregiverSnapshot.exists()) {
                                        phoneNumber = caregiverSnapshot.getString("phoneNumber")
                                    } else {
                                        Toast.makeText(activity, "Caregiver data not found.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(activity, "Error fetching caregiver data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(activity, "Caregiver ID not found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, "Patient data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Error fetching patient data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(activity, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall(number: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        activity.startActivity(intent)
    }

    private fun getLocationAndSendSMS() {
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    // Now, check for SMS permission
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        // Request SMS permission
                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), REQUEST_SMS_PERMISSION)
                    } else {
                        // Permission granted, send SMS
                        sendSMSWithLocation()
                        // Save location to Firestore
                        saveLocationToFirestore(location)
                    }
                } else {
                    Toast.makeText(activity, "Unable to get current location.", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(activity, "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendSMSWithLocation() {
        if (phoneNumber.isNullOrEmpty() || currentLocation == null) {
            Toast.makeText(activity, "Cannot send SMS without phone number and location.", Toast.LENGTH_SHORT).show()
            return
        }

        val latitude = currentLocation!!.latitude
        val longitude = currentLocation!!.longitude

        // Create Google Maps link
        val mapsLink = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"

        val message = "Patient's current location (also available in your MindMate app): $mapsLink"

        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(activity, "SMS sent successfully.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(activity, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLocationToFirestore(location: Location) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val patientUid = currentUser.uid
            val locationData = mapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "timestamp" to System.currentTimeMillis() // Optionally add a timestamp
            )
            // Save location under patient's document (or a separate collection if preferred)
            db.collection("patients").document(patientUid)
                .update("lastKnownLocation", locationData)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Location saved successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Failed to save location: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(activity, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    // Call this method from your Activity's onRequestPermissionsResult
    fun handleRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CALL_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, make the call
                    if (!phoneNumber.isNullOrEmpty()) {
                        makePhoneCall(phoneNumber!!)
                    }
                } else {
                    // Permission denied
                    Toast.makeText(activity, "Permission to make calls was denied.", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, get location and send SMS
                    getLocationAndSendSMS()
                } else {
                    // Permission denied
                    Toast.makeText(activity, "Permission to access location was denied.", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, send SMS
                    sendSMSWithLocation()
                    currentLocation?.let { saveLocationToFirestore(it) }
                } else {
                    // Permission denied
                    Toast.makeText(activity, "Permission to send SMS was denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}