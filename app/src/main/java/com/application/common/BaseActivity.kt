package com.application.common

import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import com.application.mindmate.R

open class BaseActivity : AppCompatActivity() {
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        val textView = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        // change text color to white
        textView.setTextColor(ContextCompat.getColor(this@BaseActivity, android.R.color.white))
        // change text size
        textView.maxLines = 3
        textView.textSize = 16f
        textView.gravity = Gravity.CENTER
        if (errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,
                R.color.snackBarFailure
            ))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,
                R.color.snackBarSuccess
            ))
        }
        snackBar.show()
    }
}