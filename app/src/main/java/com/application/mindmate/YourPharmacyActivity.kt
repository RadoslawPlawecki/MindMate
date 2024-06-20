package com.application.mindmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class YourPharmacyActivity : AppCompatActivity() {

    private lateinit var btnAddMedicine: Button
    private lateinit var btnViewMedicines: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_pharmacy)

        // Initialize buttons
        btnAddMedicine = findViewById(R.id.btn_add_medicine)
        btnViewMedicines = findViewById(R.id.btn_view_medicines)

        // Set up onClickListeners
        btnAddMedicine.setOnClickListener {
            val intent = Intent(this@YourPharmacyActivity, AddMedicineActivity::class.java)
            startActivity(intent)
        }

        btnViewMedicines.setOnClickListener {
            val intent = Intent(this@YourPharmacyActivity, ViewMedicinesActivity::class.java)
            startActivity(intent)
        }
    }
}