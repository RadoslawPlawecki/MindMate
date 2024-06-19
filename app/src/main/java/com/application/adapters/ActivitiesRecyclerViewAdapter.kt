package com.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.mindmate.R

class ActivitiesRecyclerViewAdapter(private val context: Context, private val activitiesModels: ArrayList<String>) : RecyclerView.Adapter<ActivitiesRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.daily_checklist_row_standard, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bind(activitiesModels[position])
    }

    override fun getItemCount(): Int {
        return activitiesModels.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityNameTextView: TextView = itemView.findViewById(R.id.example_activity_text_view)
        private val checkImageView: ImageView = itemView.findViewById(R.id.check_image)
        fun bind(activityName: String) {
            activityNameTextView.text = activityName
        }
    }
}