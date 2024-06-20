package com.application.mindmate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.mindmate.databinding.ActivityAddMedicineBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMedicineBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveMedicine.setOnClickListener {
            val name = binding.etMedicineName.text.toString().trim()
            val dose = binding.etDose.text.toString().trim()
            val time = binding.etTime.text.toString().trim()

            if (name.isNotEmpty() && dose.isNotEmpty() && time.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val medicine = hashMapOf(
                            "name" to name,
                            "dose" to dose,
                            "time" to time,
                            "userId" to userId
                        )
                        db.collection("medicines").add(medicine).await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddMedicineActivity, "Medicine added", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddMedicineActivity, "Error adding medicine: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}