package com.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.mindmate.R
import com.application.models.MedicineModel

class MedicinesRecyclerViewAdapter(private val context: Context, private val medicinesModels: ArrayList<MedicineModel>, private val onDeleteClick: (String) -> Unit) : RecyclerView.Adapter<MedicinesRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.itm_medicine, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bind(medicinesModels[position].getName(), medicinesModels[position].getTime(), medicinesModels[position].getDose())
    }

    override fun getItemCount(): Int {
        return medicinesModels.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var medicineTimeTextView: TextView =
            itemView.findViewById(R.id.tv_time)
        private var medicineNameTextView: TextView =
            itemView.findViewById(R.id.tv_name)
        private var medicineDoseTextView: TextView =
            itemView.findViewById(R.id.tv_dose)
        private var removeLinearLayout: LinearLayout =
            itemView.findViewById(R.id.btn_remove)

        fun bind(medicineName: String, time: String, dose: String) {
            medicineNameTextView.text = medicineName
            medicineTimeTextView.text = time
            medicineDoseTextView.text = dose
            removeLinearLayout.setOnClickListener {
                onDeleteClick(medicineName)
            }
        }
    }

    fun addItem(medicineName: String, time: String, dose: String) {
        medicinesModels.add(MedicineModel(medicineName, time, dose))
        notifyItemInserted(medicinesModels.size - 1)
    }

    fun removeItem(medicineName: String) {
        var positionToRemove = -1
        for ((index, model) in medicinesModels.withIndex()) {
            if (model.getName() == medicineName) {
                positionToRemove = index
                break
            }
        }
        if (positionToRemove != -1) {
            medicinesModels.removeAt(positionToRemove)
            notifyItemRemoved(positionToRemove)
        }
    }
}