package com.application.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.application.mindmate.Medicine
import com.application.mindmate.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MedicinesAdapter(private val medicines: MutableList<Medicine>) :
    RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.itm_medicine, parent, false)
        return MedicineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.tvName.text = medicine.name
        holder.tvDose.text = medicine.dose
        holder.tvTime.text = medicine.time

        holder.btnRemove.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Assuming you have the document ID stored in the Medicine object.
                    val db = FirebaseFirestore.getInstance()
                    val documentId = medicine.documentId
                    db.collection("medicines").document(documentId).delete().await()

                    withContext(Dispatchers.Main) {
                        medicines.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(holder.itemView.context, "Medicine removed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(holder.itemView.context, "Error removing medicine: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvDose: TextView = itemView.findViewById(R.id.tv_dose)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val btnRemove: Button = itemView.findViewById(R.id.btn_remove)
    }
}