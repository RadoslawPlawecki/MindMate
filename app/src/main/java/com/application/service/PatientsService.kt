import android.content.Context
import android.util.Log
import com.application.common.CommonUsage
import com.application.enums.UserRole
import com.application.service.NotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PatientsService {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    suspend fun addPatient(firebaseUser: FirebaseUser, name: String, dateOfBirth: String,
                           caregiverEmail: String, gender: String, patientEmail: String,
                           caregiverPhoneNumber: String?) {
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
            if (!caregiverPhoneNumber.isNullOrEmpty()) {
                updateCaregiverPhoneNumber(caregiverId, caregiverPhoneNumber)
            }
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            Log.e("PatientsService", "Error adding patient: ${e.message}", e)
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

    private suspend fun updateCaregiverPhoneNumber(caregiverId: String, phoneNumber: String) {
        try {
            db.collection("caregivers").document(caregiverId)
                .update("phoneNumber", phoneNumber)
                .await()
            Log.d("PatientsService", "Caregiver phone number updated: $caregiverId")
        } catch (e: Exception) {
            Log.e("PatientsService", "Error updating caregiver phone number: ${e.message}", e)
        }
    }
    fun notifyCaregiverToSetAccount(context: Context, phoneNumber: String, name: String) {
        try {
            val notificationService = NotificationService(context)
            notificationService.sendSMS(
                phoneNumber = phoneNumber,
                message = "Hello! You have been invited by $name to become their caregiver. To " +
                        "set up your caregiver account, please install the app: LINK HERE. Best " +
                        "regards, MM Team"
            )
            Log.d("PatientsService", "Caregiver notification sent to: $phoneNumber")
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