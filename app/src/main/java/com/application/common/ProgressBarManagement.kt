package com.application.common

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.application.mindmate.R

object ProgressBarManagement {
    fun setupSomethingTextWatcher(editTextSomething: EditText,
                              updateProgressBarSomething: (isSomething: Boolean) -> Unit,
                              somethingProgressAdded: Boolean) {
        var isProgressAdded = somethingProgressAdded
        editTextSomething.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isSomethingFilled = !s.isNullOrEmpty()

                if (isSomethingFilled && !isProgressAdded) {
                    updateProgressBarSomething(true)
                    isProgressAdded = true
                } else if (!isSomethingFilled && isProgressAdded) {
                    updateProgressBarSomething(false)
                    isProgressAdded = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}