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
import com.application.mindmate.caregiver.PatientLocationActivity
import com.application.mindmate.caregiver.ChatActivity
import com.application.models.PatientModel

class PatientsRecyclerViewAdapter(
    private val context: Context,
    private val patientsModels: ArrayList<PatientModel>,
    private val caregiverId: String // Add caregiverId to pass it to ChatActivity
) : RecyclerView.Adapter<PatientsRecyclerViewAdapter.MyViewHolder>() {

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
        private var patientNameTextView: TextView = itemView.findViewById(R.id.text_patient_name)
        private var patientInfoImageView: ImageView = itemView.findViewById(R.id.ic_patient_info)
        private var patientLocationImageView: ImageView = itemView.findViewById(R.id.ic_patient_location)
        private var messageImageView: ImageView = itemView.findViewById(R.id.ic_message)
        private var unreadIndicator: ImageView = itemView.findViewById(R.id.unread_message)

        fun bind(patient: PatientModel) {
            patientNameTextView.text = patient.name

            // Handle Patient Info click
            patientInfoImageView.setOnClickListener {
                val intent = Intent(itemView.context, PatientInfoActivity::class.java)
                intent.putExtra("PATIENT_ID", patient.id)
                itemView.context.startActivity(intent)
            }

            // Handle Patient Location click
            patientLocationImageView.setOnClickListener {
                val intent = Intent(itemView.context, PatientLocationActivity::class.java)
                intent.putExtra("PATIENT_ID", patient.id)
                itemView.context.startActivity(intent)
            }

            // Handle Message click
            messageImageView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("PATIENT_ID", patient.id)
                intent.putExtra("PATIENT_NAME", patient.name)
                intent.putExtra("CAREGIVER_ID", caregiverId)
                itemView.context.startActivity(intent)
            }

            // TODO: Update unreadIndicator visibility based on unread messages
            unreadIndicator.visibility = View.GONE // Set to VISIBLE when there are unread messages
        }
    }
}