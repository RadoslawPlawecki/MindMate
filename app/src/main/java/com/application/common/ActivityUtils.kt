package com.application.common

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.application.enums.UserRole
import com.application.mindmate.AlarmActivity
import com.application.mindmate.both.MenuActivity
import com.application.mindmate.R

/**
 * The Kotlin object to encapsulate movement between activities.
 */
object ActivityUtils {
    /**
     * The method to set up an action bar.
     * @param activity activity where the action bar is located.
     */
    fun actionBarSetup(activity: Activity, intentValue: UserRole) {
        val openMenu = activity.findViewById<ImageView>(R.id.image_bars)
        openMenu.setOnClickListener {
            val intent = Intent(activity, MenuActivity::class.java)
            intent.putExtra("ROLE", intentValue.toString())
            activity.startActivity(intent)
        }

        val alarmImageView = activity.findViewById<ImageView>(R.id.alarm)
        alarmImageView.setOnClickListener {
            AlarmActivity(activity).useAlarm()
        }
    }

    /**
     * The method to set up a shorter way to code movement between activities.
     * @param T type of the view.
     * @param layoutResourceId the resource id of the view to set the click listener on.
     * @param activity the current activity.
     * @param targetActivity the target activity.
     */
    fun <T : View> changeActivity(layoutResourceId: Int, activity: Activity, targetActivity: Activity) {
        val element = activity.findViewById<T>(layoutResourceId)
        element.setOnClickListener {
            val intent = Intent(activity, targetActivity::class.java)
            activity.startActivity(intent)
        }
    }

    fun <T : View> changeActivityWithIntent(layoutResourceId: Int, activity: Activity, targetActivity: Activity, name: String, value: String) {
        val element = activity.findViewById<T>(layoutResourceId)
        element.setOnClickListener {
            val intent = Intent(activity, targetActivity::class.java)
            intent.putExtra(name, value)
            activity.startActivity(intent)
        }
    }
}