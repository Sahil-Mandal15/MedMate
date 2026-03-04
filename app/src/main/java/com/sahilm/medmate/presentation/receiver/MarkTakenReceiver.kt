package com.sahilm.medmate.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.sahilm.medmate.domain.repository.HomeRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarkTakenReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MarkTakenEntryPoint {
        fun homeRepository(): HomeRepository
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val reminderId = intent?.getIntExtra("reminder_id", -1) ?: return
        if (reminderId == -1) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            MarkTakenEntryPoint::class.java
        )
        val repository = entryPoint.homeRepository()

        CoroutineScope(Dispatchers.IO).launch {
            repository.markReminderAsTaken(reminderId)
        }

        // Dismiss the notification immediately
        NotificationManagerCompat.from(context).cancel(reminderId)
    }
}

