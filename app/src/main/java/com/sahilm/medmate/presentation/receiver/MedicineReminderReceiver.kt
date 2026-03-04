package com.sahilm.medmate.presentation.receiver

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sahilm.medmate.R
import com.sahilm.medmate.domain.model.MedicineReminderModel
import com.sahilm.medmate.presentation.activity.MainActivity
import com.sahilm.medmate.utils.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MedicineReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        val reminderInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("reminder_info", MedicineReminderModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getSerializableExtra("reminder_info") as? MedicineReminderModel
        } ?: return

        // Reschedule for next day if repeatDaily is enabled
        if (reminderInfo.repeatDaily) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val nextDate = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(cal.time)
            AlarmScheduler.schedule(context, reminderInfo.copy(date = nextDate))
        }

        // Tap the notification → open MainActivity
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, reminderInfo.id, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Mark as Taken" action → MarkTakenReceiver
        val markTakenIntent = Intent(context, MarkTakenReceiver::class.java).apply {
            putExtra("reminder_id", reminderInfo.id)
        }
        val markTakenPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderInfo.id + 10_000,
            markTakenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "medmate_channel")
            .setContentTitle("Medicine Reminder 💊")
            .setContentText("Time to take ${reminderInfo.medicineName}")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_alarm, context.getString(R.string.mark_as_taken), markTakenPendingIntent)
            .build()

        val canNotify = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (canNotify) {
            NotificationManagerCompat.from(context).notify(reminderInfo.id, notification)
        }
    }
}

