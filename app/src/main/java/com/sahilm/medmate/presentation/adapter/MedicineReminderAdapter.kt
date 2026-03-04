package com.sahilm.medmate.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sahilm.medmate.R
import com.sahilm.medmate.domain.model.MedicineReminderModel

class MedicineReminderAdapter(
    private val onDelete: (MedicineReminderModel) -> Unit,
    private val onMarkTaken: (MedicineReminderModel) -> Unit
) : ListAdapter<MedicineReminderModel, MedicineReminderAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<MedicineReminderModel>() {
            override fun areItemsTheSame(a: MedicineReminderModel, b: MedicineReminderModel) = a.id == b.id
            override fun areContentsTheSame(a: MedicineReminderModel, b: MedicineReminderModel) = a == b
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_medicine_name)
        val tvDesc: TextView = view.findViewById(R.id.tv_description)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvRepeat: TextView = view.findViewById(R.id.tv_repeat)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        val btnMarkTaken: ImageButton = view.findViewById(R.id.btn_mark_taken)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine_reminder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvName.text = item.medicineName
        holder.tvDesc.text = item.description.ifBlank { "No notes" }
        holder.tvDate.text = item.date
        holder.tvTime.text = item.time
        holder.tvRepeat.visibility = if (item.repeatDaily) View.VISIBLE else View.GONE
        holder.btnDelete.setOnClickListener { onDelete(item) }
        holder.btnMarkTaken.setOnClickListener { onMarkTaken(item) }
    }
}
