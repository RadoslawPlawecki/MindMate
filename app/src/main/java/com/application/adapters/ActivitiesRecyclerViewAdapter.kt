package com.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.mindmate.R
import com.application.service.ActivitiesService

class ActivitiesRecyclerViewAdapter(private val context: Context, private val activitiesModels: ArrayList<String>, private val onDeleteClick: (String) -> Unit) : RecyclerView.Adapter<ActivitiesRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.itm_daily_checklist, parent, false)
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

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var activityNameTextView: TextView =
            itemView.findViewById(R.id.example_activity_text_view)
        private var checkImageView: ImageView = itemView.findViewById(R.id.check_image)
        private var checkBackgroundImageView: ImageView =
            itemView.findViewById(R.id.check_background_image)
        private var deleteImageView: ImageView = itemView.findViewById(R.id.trash_image)

        init {
            checkBackgroundImageView.setOnClickListener {
                toggleCheckVisibility()
            }
        }

        private fun toggleCheckVisibility() {
            val activitiesService = ActivitiesService()
            val activityName = activityNameTextView.text.toString()
            activitiesService.fetchActivityStatus(activityName) { status ->
                if (status == true) {
                    activitiesService.updateActivityStatus(activityName, false)
                    checkImageView.visibility = View.GONE
                } else {
                    activitiesService.updateActivityStatus(activityName, true)
                    checkImageView.visibility = View.VISIBLE
                }
            }
        }

        fun bind(activityName: String) {
            activityNameTextView.text = activityName
            val activitiesService = ActivitiesService()
            activitiesService.fetchActivityStatus(activityName) { status ->
                checkImageView.visibility = if (status == true) View.VISIBLE else View.GONE
            }
            deleteImageView.setOnClickListener {
                onDeleteClick(activityName)
            }
        }
    }

    fun addItem(activityName: String) {
        activitiesModels.add(activityName)
        notifyItemInserted(activitiesModels.size - 1)
    }

    fun removeItem(activityName: String) {
        val position = activitiesModels.indexOf(activityName)
        if (position != -1) {
            activitiesModels.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}