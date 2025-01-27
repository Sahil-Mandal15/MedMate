package com.sahilm.medmate.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.sahilm.medmate.BuildConfig
import com.sahilm.medmate.R
import com.sahilm.medmate.auth.AuthClient
import com.sahilm.medmate.databinding.FragmentHomeScreenBinding
import com.sahilm.medmate.repository.MedmateRepository
import com.sahilm.medmate.viewmodel.MedMateViewModel
import kotlinx.coroutines.launch

class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var medMateViewModel: MedMateViewModel
    private lateinit var authClient: AuthClient
    private var aiBtnClickCounter = 0

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

        if (aiBtnClickCounter != 1){
            binding.llIntroMedMate.visibility = View.GONE
        }

        val medmateRepository = MedmateRepository()
        medMateViewModel = MedMateViewModel(medmateRepository, requireContext())
         authClient = AuthClient(requireContext())

        lifecycleScope.launch{
             medMateViewModel.foodFactDetails.collect { details ->
                binding.tvDailyFoodFactDisplay.text = details.foodFact
            }
        }

        lifecycleScope.launch {
            authClient.loginState.collect { user ->
                Glide.with(this@HomeScreenFragment)
                    .load(user.userPhotoUri)
                    .into(binding.ivUserPfp)
                val userName = user.userName.split(" ")[0]
                binding.tvUserName.text = "${userName.uppercase()},"
            }
        }

        binding.ivUserPfp.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_profileScreenFragment)
        }

        binding.btnGetStartedAi.setOnClickListener {
            aiBtnClickCounter ++

                findNavController().navigate(R.id.action_homeScreenFragment_to_chatBotFragment)

        }
    }
}