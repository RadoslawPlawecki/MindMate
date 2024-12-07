package com.application.mindmate.caregiver

import PatientsService
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adapters.PatientsRecyclerViewAdapter
import com.application.common.ActivityUtils
import com.application.common.CommonUsage
import com.application.enums.UserRole
import com.application.mindmate.R
import com.application.models.PatientModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CaregiverDashboardActivity : AppCompatActivity() {
    private lateinit var helloTextView: TextView
    private lateinit var daysOfUseTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var loadingTextView: TextView
    private lateinit var helloUserLinearLayout: LinearLayout
    private lateinit var patientsLinearLayout: LinearLayout
    private lateinit var patientsService: PatientsService
    private var patientsModels: ArrayList<PatientModel> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientsRecyclerViewAdapter
    private lateinit var caregiverId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_dashboard)
        ActivityUtils.actionBarSetup(this, UserRole.CAREGIVER)
        patientsService = PatientsService()
        helloTextView = findViewById(R.id.hello)
        loadingProgressBar = findViewById(R.id.pb_dashboard)
        loadingTextView = findViewById(R.id.loading_data_text)
        helloUserLinearLayout = findViewById(R.id.hello_ll)
        patientsLinearLayout = findViewById(R.id.patients_ll)
        daysOfUseTextView = findViewById(R.id.days_of_use)
        recyclerView = findViewById(R.id.recycler_view)
        getPatients()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            caregiverId = currentUser.uid
        } else {
            finish()
            return
        }

        adapter = PatientsRecyclerViewAdapter(this, patientsModels, caregiverId)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.Main).launch {
            helloUser()
            displayUserStreak()
            closeLoading()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPatients() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val patients = withContext(Dispatchers.IO) {
                    patientsService.fetchPatients()
                }
                patientsModels.clear()
                patientsModels.addAll(patients)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun helloUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("caregivers").document(uid)
            try {
                val documentSnapshot = withContext(Dispatchers.IO) {
                    userRef.get().await()
                }
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name") ?: "User"
                    helloTextView.text = "Hello $name!"
                } else {
                    helloTextView.text = "Hello User!"
                }
            } catch (e: Exception) {
                helloTextView.text = "Hello User!"
                e.printStackTrace()
            }
        }
    }

    private suspend fun displayUserStreak() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("caregivers").document(uid)
            try {
                val documentSnapshot = withContext(Dispatchers.IO) {
                    userRef.get().await()
                }
                if (documentSnapshot.exists()) {
                    val lastLoginDate = documentSnapshot.getString("lastLoginDate")
                    val streak = documentSnapshot.getLong("streak")?.toInt() ?: 0
                    val currentDate = CommonUsage.getCurrentDate()
                    val dateDiffInDays = CommonUsage.getDateDifference(lastLoginDate.toString(), currentDate)
                    val updatedStreak = when (dateDiffInDays) {
                        0.toLong() -> {
                            streak
                        }
                        1.toLong() -> {
                            streak + 1
                        }
                        else -> {
                            1
                        }
                    }
                    if (lastLoginDate != currentDate) {
                        userRef.update("lastLoginDate", currentDate, "streak", updatedStreak).await()
                    }
                    val streakText = if (updatedStreak != 1) {
                        getString(R.string.you_ve_been_using_mindmate_for_x_days_in_a_row, updatedStreak)
                    } else {
                        getString(R.string.you_ve_been_using_mindmate_for_1_day, updatedStreak)
                    }
                    daysOfUseTextView.text = streakText
                } else {
                    val initialStreak = 1
                    val currentDate = CommonUsage.getCurrentDate()
                    userRef.set(mapOf("lastLoginDate" to currentDate, "streak" to initialStreak)).await()
                    val streakText = getString(R.string.you_ve_been_using_mindmate_for_1_day, initialStreak)
                    daysOfUseTextView.text = streakText
                }
            } catch (e: Exception) {
                daysOfUseTextView.text = getString(R.string.you_ve_been_using_mindmate_for_1_day, 1)
                e.printStackTrace()
            }
        }
    }

    private fun closeLoading() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                loadingProgressBar.visibility = View.GONE
                loadingTextView.visibility = View.GONE
                helloUserLinearLayout.visibility = View.VISIBLE
                patientsLinearLayout.visibility = View.VISIBLE
                helloUserLinearLayout.startAnimation(fadeIn)
                patientsLinearLayout.startAnimation(fadeIn)
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        loadingProgressBar.startAnimation(fadeOut)
        loadingTextView.startAnimation(fadeOut)
    }
}