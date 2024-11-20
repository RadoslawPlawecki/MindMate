import android.content.Context
import android.util.Log
import com.application.common.CommonUsage
import com.application.enums.UserRole
import com.application.models.PatientModel
import com.application.service.NotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PatientsService {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    suspend fun addPatient(firebaseUser: FirebaseUser, name: String, dateOfBirth: String,
                           caregiverEmail: String, gender: String, patientEmail: String) {
        try {
            val caregiverId = getCaregiverByEmail(caregiverEmail)
            if (caregiverId == null) {
                Log.e("PatientsService", "Caregiver not found for email: $caregiverEmail")
                throw IllegalStateException("Caregiver account not found. Patient registration aborted.")
            }
            val patientData = hashMapOf(
                "uid" to firebaseUser.uid,
                "email" to patientEmail,
                "name" to name,
                "dateOfBirth" to dateOfBirth,
                "gender" to gender,
                "role" to UserRole.PATIENT,
                "lastLoginDate" to CommonUsage.getCurrentDate(),
                "streak" to 1,
                "caregiverId" to caregiverId
            )
            db.collection("patients").document(firebaseUser.uid).set(patientData).await()
            Log.d("PatientsService", "Patient document added: ${firebaseUser.uid}")
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            Log.e("PatientsService", "Error adding patient: ${e.message}", e)
        }
    }

    suspend fun fetchPatients(): ArrayList<PatientModel> {
        val patients = ArrayList<PatientModel>()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val patientsRef = db.collection("patients")
            try {
                val querySnapshot = patientsRef.whereEqualTo("caregiverId", uid).get().await()
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        val patientId = document.id
                        val patientName = document.getString("name")
                        if (patientName != null) {
                            patients.add(PatientModel(id = patientId, name = patientName))
                        }
                    }
                } else {
                    Log.e("FetchPatients", "No patients found for caregiverId: $uid")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return patients
    }

    suspend fun fetchPatientById(patientId: String): Map<String, String?>? {
        return try {
            val document = db.collection("patients").document(patientId).get().await()
            if (document.exists()) {
                document.data?.mapValues { it.value?.toString() }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("PatientsService", "Error fetching patient by ID: ${e.message}", e)
            null
        }
    }

    private suspend fun getCaregiverByEmail(caregiverEmail: String): String? {
        return try {
            val querySnapshot = db.collection("caregivers")
                .whereEqualTo("email", caregiverEmail)
                .get()
                .await()
            querySnapshot.documents.firstOrNull()?.id
        } catch (e: Exception) {
            Log.e("PatientsService", "Error fetching caregiver ID: ${e.message}", e)
            null
        }
    }
    fun notifyCaregiverToSetAccount(context: Context, caregiverEmail: String, name: String) {
        try {
            val notificationService = NotificationService(context)
            notificationService.sendSMS(
                phoneNumber = "606670149",
                message = "Hello! You have been invited by $name to become their caregiver. To " +
                        "set up your caregiver account, please install the app: LINK HERE. Best " +
                        "regards, MM Team"
            )
            Log.d("PatientsService", "Caregiver notification sent to: $caregiverEmail")
        } catch (e: Exception) {
            Log.e("PatientsService", "Error sending notification: ${e.message}", e)
        }
    }

    suspend fun caregiverExists(caregiverEmail: String): Boolean {
        return try {
            val querySnapshot = db.collection("caregivers")
                .whereEqualTo("email", caregiverEmail)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("PatientsService", "Error checking caregiver existence: ${e.message}", e)
            false
        }
    }
}