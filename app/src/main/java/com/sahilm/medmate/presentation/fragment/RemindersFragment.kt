package com.sahilm.medmate.presentation.fragment

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sahilm.medmate.R
import com.sahilm.medmate.presentation.adapter.MedicineReminderAdapter
import com.sahilm.medmate.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemindersFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: MedicineReminderAdapter

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permission result handled; alarm still works even without notification on older APIs */ }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_reminders, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val rvReminders = view.findViewById<RecyclerView>(R.id.rv_reminders)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layout_empty)
        val tvCount = view.findViewById<TextView>(R.id.tv_count)

        adapter = MedicineReminderAdapter(
            onDelete = { reminder ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Reminder")
                    .setMessage("Remove reminder for ${reminder.medicineName}?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteMedicineReminder(requireContext(), reminder.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onMarkTaken = { reminder ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Mark as Taken")
                    .setMessage("Mark ${reminder.medicineName} as taken?")
                    .setPositiveButton("Mark Taken") { _, _ ->
                        viewModel.markReminderAsTaken(requireContext(), reminder.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        rvReminders.layoutManager = LinearLayoutManager(requireContext())
        rvReminders.adapter = adapter

        viewModel.medicineReminders.observe(viewLifecycleOwner) { list ->
            tvCount.text = list?.size?.toString() ?: "0"
            if (list.isNullOrEmpty()) {
                layoutEmpty.visibility = View.VISIBLE
                rvReminders.visibility = View.GONE
            } else {
                layoutEmpty.visibility = View.GONE
                rvReminders.visibility = View.VISIBLE
                adapter.submitList(list)
            }
        }

        fab.setOnClickListener {
            requestPermissionsIfNeeded()
            val sheet = AddReminderBottomSheet()
            sheet.onSave = { reminder ->
                viewModel.addMedicineReminder(requireContext(), reminder)
            }
            sheet.show(parentFragmentManager, AddReminderBottomSheet.TAG)
        }

        viewModel.getAllMedicineReminders()
    }

    private fun requestPermissionsIfNeeded() {
        // Request POST_NOTIFICATIONS on API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        // Check exact alarm permission on API 31+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Exact Alarm Permission")
                    .setMessage(getString(R.string.exact_alarm_permission))
                    .setPositiveButton("Open Settings") { _, _ ->
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = "package:${requireContext().packageName}".toUri()
                        }
                        startActivity(intent)
                    }
                    .setNegativeButton("Skip", null)
                    .show()
            }
        }
    }
}




