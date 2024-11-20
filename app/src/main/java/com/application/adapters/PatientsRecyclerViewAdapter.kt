package com.application.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.mindmate.R
import com.application.mindmate.caregiver.PatientInfoActivity
import com.application.models.PatientModel

class PatientsRecyclerViewAdapter(private val context: Context, private val patientsModels: ArrayList<PatientModel>) : RecyclerView.Adapter<PatientsRecyclerViewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.itm_patient, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bind(patientsModels[position])
    }

    override fun getItemCount(): Int {
        return patientsModels.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var patientNameTextView: TextView =
            itemView.findViewById(R.id.text_patient_name)
        private var patientInfoImageView: ImageView = itemView.findViewById(R.id.ic_patient_info)

        fun bind(patient: PatientModel) {
            patientNameTextView.text = patient.name
            patientInfoImageView.setOnClickListener {
                val intent = Intent(itemView.context, PatientInfoActivity::class.java)
                intent.putExtra("PATIENT_ID", patient.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}