package com.sahilm.medmate.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sahilm.medmate.domain.model.MedicineReminderModel
import com.sahilm.medmate.presentation.activity.MainActivity
import com.sahilm.medmate.presentation.receiver.MedicineReminderReceiver
import java.text.SimpleDateFormat
import java.util.Locale

object AlarmScheduler {

    fun schedule(context: Context, reminder: MedicineReminderModel) {
        val triggerAtMillis = parseTriggerMillis(reminder.date, reminder.time) ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, MedicineReminderReceiver::class.java).apply {
            putExtra("reminder_info", reminder)
        }
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id,
            showIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val clockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
        alarmManager.setAlarmClock(clockInfo, alarmPendingIntent)
    }

    fun cancel(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, MedicineReminderReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(alarmPendingIntent)
        alarmPendingIntent.cancel()
    }

    private fun parseTriggerMillis(date: String, time: String): Long? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.parse("$date $time")?.time
        } catch (_: Exception) {
            null
        }
    }
}

