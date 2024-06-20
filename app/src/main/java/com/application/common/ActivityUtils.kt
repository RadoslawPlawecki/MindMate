package com.application.common

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import com.application.mindmate.AlarmActivity
import com.application.mindmate.MenuActivity
import com.application.mindmate.R

object ActivityUtils {
    fun actionBarSetup(activity: Activity) {
        val openMenu = activity.findViewById<ImageView>(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(activity, MenuActivity::class.java)
            activity.startActivity(intent)
        }

        val alarmImageView = activity.findViewById<ImageView>(R.id.alarm)
        alarmImageView.setOnClickListener {
            AlarmActivity(activity).useAlarm()
        }
    }
}