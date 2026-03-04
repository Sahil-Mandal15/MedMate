package com.sahilm.medmate.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sahilm.medmate.R
import com.sahilm.medmate.presentation.adapter.MedicineReminderAdapter
import com.sahilm.medmate.presentation.viewmodel.HomeViewModel
import com.sahilm.medmate.utils.ResultState
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: MedicineReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvGreeting = view.findViewById<TextView>(R.id.tv_greeting)
        val tvFoodFact = view.findViewById<TextView>(R.id.tv_food_fact)
        val progressFact = view.findViewById<ProgressBar>(R.id.progress_fact)
        val tvSeeAll = view.findViewById<TextView>(R.id.tv_see_all)
        val tvNoReminders = view.findViewById<TextView>(R.id.tv_no_reminders_home)
        val rvReminders = view.findViewById<RecyclerView>(R.id.rv_reminders_home)

        // Greeting based on time of day
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        tvGreeting.text = when {
            hour < 12 -> "Good Morning 🌅"
            hour < 17 -> "Good Afternoon ☀️"
            else -> "Good Evening 🌙"
        }

        // Set up adapter (home shows first 3 only, no actions)
        adapter = MedicineReminderAdapter(onDelete = {}, onMarkTaken = {})
        rvReminders.layoutManager = LinearLayoutManager(requireContext())
        rvReminders.adapter = adapter

        tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.remindersFragment)
        }

        // Food fact
        viewModel.foodFact.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    progressFact.visibility = View.VISIBLE
                    tvFoodFact.visibility = View.GONE
                }
                is ResultState.Success -> {
                    progressFact.visibility = View.GONE
                    tvFoodFact.visibility = View.VISIBLE
                    tvFoodFact.text = state.data.foodFact
                }
                is ResultState.Error -> {
                    progressFact.visibility = View.GONE
                    tvFoodFact.visibility = View.VISIBLE
                    tvFoodFact.text = state.message
                }
            }
        }

        // Reminders preview
        viewModel.medicineReminders.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                tvNoReminders.visibility = View.VISIBLE
                rvReminders.visibility = View.GONE
            } else {
                tvNoReminders.visibility = View.GONE
                rvReminders.visibility = View.VISIBLE
                adapter.submitList(list.take(3))
            }
        }

        viewModel.getAllMedicineReminders()
    }
}

