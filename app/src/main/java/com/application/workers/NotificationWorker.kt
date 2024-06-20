package com.application.mindmate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.checkSelfPermission

class NotificationWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Medication Reminder"
        val message = inputData.getString("message") ?: "It's time to take your medicine"
        val medicineName = inputData.getString("medicineName")
        val dose = inputData.getString("dose")

        createNotificationChannel()

        if (checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            sendNotification(title, message, medicineName, dose)
        } else {
            Toast.makeText(context, "Notification permission is not granted", Toast.LENGTH_SHORT).show()
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medicine Reminder Channel"
            val descriptionText = "Channel for Medicine Reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MEDICINE_REMINDER", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, message: String, medicineName: String?, dose: String?) {
        val intent = Intent(context, TitleActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "MEDICINE_REMINDER")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your custom icon
            .setContentTitle(title)
            .setContentText("$medicineName - $dose")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
            } else {
                Toast.makeText(context, "Notification permission is not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}