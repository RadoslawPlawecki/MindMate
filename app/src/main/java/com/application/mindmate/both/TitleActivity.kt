package com.application.mindmate.both

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.mindmate.R
import com.google.firebase.FirebaseApp

class TitleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
        FirebaseApp.initializeApp(this)
        ActivityUtils.changeActivity<Button>(R.id.getting_started, this, ChooseRoleActivity())
    }
}