package com.sahilm.medmate.presentation.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.button.MaterialButton
import com.sahilm.medmate.R
import com.sahilm.medmate.domain.model.MedicineReminderModel
import java.util.Calendar
import java.util.Locale

class AddReminderBottomSheet : BottomSheetDialogFragment() {

    var onSave: ((MedicineReminderModel) -> Unit)? = null

    private lateinit var etMedicineName: TextInputEditText
    private lateinit var tilMedicineName: TextInputLayout
    private lateinit var etDescription: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var tilDate: TextInputLayout
    private lateinit var etTime: TextInputEditText
    private lateinit var tilTime: TextInputLayout
    private lateinit var switchRepeat: MaterialSwitch
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_add_reminder, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etMedicineName = view.findViewById(R.id.et_medicine_name)
        tilMedicineName = view.findViewById(R.id.til_medicine_name)
        etDescription = view.findViewById(R.id.et_description)
        etDate = view.findViewById(R.id.et_date)
        tilDate = view.findViewById(R.id.til_date)
        etTime = view.findViewById(R.id.et_time)
        tilTime = view.findViewById(R.id.til_time)
        switchRepeat = view.findViewById(R.id.switch_repeat)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)

        // Date picker
        val dateClickListener = View.OnClickListener { showDatePicker() }
        etDate.setOnClickListener(dateClickListener)
        tilDate.setEndIconOnClickListener(dateClickListener)

        // Time picker
        val timeClickListener = View.OnClickListener { showTimePicker() }
        etTime.setOnClickListener(timeClickListener)
        tilTime.setEndIconOnClickListener(timeClickListener)

        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            if (validate()) {
                val reminder = MedicineReminderModel(
                    medicineName = etMedicineName.text.toString().trim(),
                    description = etDescription.text.toString().trim(),
                    date = etDate.text.toString().trim(),
                    time = etTime.text.toString().trim(),
                    repeatDaily = switchRepeat.isChecked
                )
                onSave?.invoke(reminder)
                dismiss()
            }
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                etDate.setText(String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day))
                tilDate.error = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = cal.timeInMillis
        }.show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                etTime.setText(String.format(Locale.ROOT, "%02d:%02d", hour, minute))
                tilTime.error = null
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun validate(): Boolean {
        var valid = true
        if (etMedicineName.text.isNullOrBlank()) {
            tilMedicineName.error = "Please enter medicine name"
            valid = false
        } else {
            tilMedicineName.error = null
        }
        if (etDate.text.isNullOrBlank()) {
            tilDate.error = "Please select a date"
            valid = false
        } else {
            tilDate.error = null
        }
        if (etTime.text.isNullOrBlank()) {
            tilTime.error = "Please select a time"
            valid = false
        } else {
            tilTime.error = null
        }
        return valid
    }

    companion object {
        const val TAG = "AddReminderBottomSheet"
    }}






