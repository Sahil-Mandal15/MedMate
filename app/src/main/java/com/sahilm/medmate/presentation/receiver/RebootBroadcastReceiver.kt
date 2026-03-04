package com.sahilm.medmate.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sahilm.medmate.domain.repository.HomeRepository
import com.sahilm.medmate.utils.AlarmScheduler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RebootBroadcastReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RebootReceiverEntryPoint {
        fun homeRepository(): HomeRepository
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val action = intent?.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != "android.intent.action.LOCKED_BOOT_COMPLETED") return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            RebootReceiverEntryPoint::class.java
        )
        val repository = entryPoint.homeRepository()

        CoroutineScope(Dispatchers.IO).launch {
            val list = repository.getAllMedicineReminders()
            for (reminder in list) AlarmScheduler.schedule(context, reminder)
        }
    }
}

