// com/application/mindmate/caregiver/PatientLocationActivity.kt

package com.application.mindmate.caregiver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.application.mindmate.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

import com.google.firebase.firestore.FirebaseFirestore

class PatientLocationActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var patientId: String? = null

    private val TAG = "PatientLocationActivity"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var patientLatLng: LatLng? = null
    private var caregiverLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to the layout that contains the MapFragment
        setContentView(R.layout.activity_patient_location)

        // Get the patient ID from intent extras
        patientId = intent.getStringExtra("PATIENT_ID")

        if (patientId == null) {
            Toast.makeText(this, "Patient ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_patient_location) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Optionally, enable the My Location layer and the related control on the map.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1)
        }

        // Fetch the patient's last known location
        fetchPatientLocation()
    }

    private fun fetchPatientLocation() {
        val db = FirebaseFirestore.getInstance()
        val patientRef = db.collection("patients").document(patientId!!)

        patientRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.contains("lastKnownLocation")) {
                        val locationData = document.get("lastKnownLocation") as Map<String, Any>
                        val latitude = locationData["latitude"] as? Double
                        val longitude = locationData["longitude"] as? Double

                        if (latitude != null && longitude != null) {
                            patientLatLng = LatLng(latitude, longitude)
                            // Add a marker for patient's location
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(patientLatLng!!)
                                    .title("Patient's Last Known Location")
                            )

                            // Now get caregiver's current location
                            getCurrentLocation { currentLocation ->
                                if (currentLocation != null) {
                                    // Add marker for caregiver's location
                                    caregiverLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(caregiverLatLng!!)
                                            .title("Your Location")
                                    )

                                    // Adjust the camera to show both locations
                                    val boundsBuilder = LatLngBounds.Builder()
                                    boundsBuilder.include(caregiverLatLng!!)
                                    boundsBuilder.include(patientLatLng!!)
                                    val bounds = boundsBuilder.build()
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))


                                    val directionsUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${caregiverLatLng!!.latitude},${caregiverLatLng!!.longitude}&destination=${patientLatLng!!.latitude},${patientLatLng!!.longitude}&travelmode=driving")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, directionsUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    if (mapIntent.resolveActivity(packageManager) != null) {
                                        startActivity(mapIntent)
                                    } else {
                                        Toast.makeText(this, "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
                                    }

                                } else {
                                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                                    // Center map on patient's location
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(patientLatLng!!, 15f))
                                }
                            }
                        } else {
                            Toast.makeText(this, "Location data is missing", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Patient's location not available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Patient document not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                Toast.makeText(this, "Failed to get patient data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions not granted, request them
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                callback(location)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting current location", exception)
                callback(null)
            }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.isMyLocationEnabled = true
                        fetchPatientLocation()
                    }
                } else {
                    // Permission denied
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore other requests
            }
        }
    }
}