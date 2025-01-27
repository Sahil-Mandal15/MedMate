package com.sahilm.medmate.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.sahilm.medmate.BuildConfig
import com.sahilm.medmate.databinding.FragmentHomeScreenBinding
import com.sahilm.medmate.repository.MedmateRepository
import com.sahilm.medmate.viewmodel.MedMateViewModel
import kotlinx.coroutines.launch

class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var medMateViewModel: MedMateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val medmateRepository = MedmateRepository()
        val medMateViewModel = MedMateViewModel(medmateRepository, requireContext())

        lifecycleScope.launch{
             medMateViewModel.foodFactDetails.collect { details ->
                binding.tvDailyFoodFactDisplay.text = details.foodFact
            }
        }
    }
}