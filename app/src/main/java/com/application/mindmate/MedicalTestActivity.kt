package com.application.mindmate
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MedicalTestActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_test)

        database = FirebaseDatabase.getInstance().getReference("MedicalTests")

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

            if (name.isNotEmpty() && dateOfBirth.isNotEmpty() && school.isNotEmpty() && date.isNotEmpty()
                && similarity.isNotEmpty() && coins.isNotEmpty() && change.isNotEmpty() && animalNames.isNotEmpty()
            ) {
                val testEntry = MedicalTestEntry(name, dateOfBirth, school, date, similarity, coins, change, animalNames)
                saveTestEntry(testEntry, name)
            } else {
                Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveTestEntry(testEntry: MedicalTestEntry, name: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    database.child(name).setValue(testEntry).await()
                }
                Toast.makeText(this@MedicalTestActivity, "Test submitted successfully", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MedicalTestActivity, "Submission failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}



