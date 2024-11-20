package com.application.mindmate.games

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.application.common.ActivityUtils
import com.application.enums.UserRole
import com.application.mindmate.R

class GuessWordChooseCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess_word_choose_category)
        val activityUtils = ActivityUtils
        ActivityUtils.actionBarSetup(this, UserRole.PATIENT)
        val layoutResourcesId = listOf(R.id.btn_fruit, R.id.btn_vegetable, R.id.btn_country,
            R.id.btn_furniture, R.id.btn_animal, R.id.btn_color, R.id.btn_instrument, R.id.btn_sport)
        val targetActivity = GuessWordActivity()
        val intentName = "WORD_CATEGORY"
        val intentValue = listOf("fruit", "vegetable", "country", "furniture",
            "animal", "color", "instrument", "sport")
        var i = 0
        while (i < intentValue.size) {
            activityUtils.changeActivityWithIntent<LinearLayout>(layoutResourcesId[i], this, targetActivity, intentName, intentValue[i])
            i++
        }
    }
}