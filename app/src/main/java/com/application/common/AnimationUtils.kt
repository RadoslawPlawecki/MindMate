package com.application.common

import android.animation.ObjectAnimator
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

object AnimationUtils {
    fun animateProgressBar(progressBar: ProgressBar, targetProgress: Int) {
        val animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, targetProgress)
        animation.duration = 1000
        animation.start()
    }
}