package com.application.mindmate

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.entries.MedicalTestEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MedicalTestActivity : AppCompatActivity() {
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() } // Get FirebaseAuth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_test)

        val nameEditText = findViewById<EditText>(R.id.name)
        val dateOfBirthEditText = findViewById<EditText>(R.id.dateOfBirth)
        val schoolEditText = findViewById<EditText>(R.id.school)
        val dateEditText = findViewById<EditText>(R.id.date)
        val similarityEditText = findViewById<EditText>(R.id.similarity)
        val coinsEditText = findViewById<EditText>(R.id.coins)
        val changeEditText = findViewById<EditText>(R.id.change)
        val animalNamesEditText = findViewById<EditText>(R.id.animalNames)
        val submitButton = findViewById<Button>(R.id.submit_button)

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val dateOfBirth = dateOfBirthEditText.text.toString().trim()
            val school = schoolEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val similarity = similarityEditText.text.toString().trim()
            val coins = coinsEditText.text.toString().trim()
            val change = changeEditText.text.toString().trim()
            val animalNames = animalNamesEditText.text.toString().trim()

            if (name.isNotEmpty() && dateOfBirth.isNotEmpty() && school.isNotEmpty() &&
                date.isNotEmpty() && similarity.isNotEmpty() && coins.isNotEmpty() &&
                change.isNotEmpty() && animalNames.isNotEmpty()) {

                val currentDate = getCurrentDate()
                val userId = auth.currentUser?.uid

                val testEntry = MedicalTestEntry(
                    name, dateOfBirth, school, date, similarity, coins, change, animalNames,
                    currentDate, userId
                )

                if (isNetworkAvailable()) {
                    saveTestEntry(testEntry, name)
                } else {
                    Log.e("MedicalTestActivity", "No network connection")
                    Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.w("MedicalTestActivity", "Input validation failed")
                Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveTestEntry(testEntry: MedicalTestEntry, name: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    firestore.collection("medical-tests")
                        .document(name)
                        .set(testEntry)
                        .await()
                }
                Log.i("MedicalTestActivity", "Test submitted successfully")
                Toast.makeText(this@MedicalTestActivity, "Test submitted successfully", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("MedicalTestActivity", "Submission failed: ${e.message}", e)
                Toast.makeText(this@MedicalTestActivity, "Submission failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}