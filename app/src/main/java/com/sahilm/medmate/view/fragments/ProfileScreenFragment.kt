package com.sahilm.medmate.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.rpc.context.AttributeContext.Auth
import com.sahilm.medmate.R
import com.sahilm.medmate.auth.AuthClient
import com.sahilm.medmate.databinding.FragmentProfileScreenBinding
import kotlinx.coroutines.launch

class ProfileScreenFragment : Fragment() {

    private var _binding: FragmentProfileScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var authClient: AuthClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentProfileScreenBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authClient = AuthClient(requireContext())

        binding.btnSignOut.setOnClickListener {
            lifecycleScope.launch {
                authClient.signOut()
                findNavController().navigate(R.id.action_profileScreenFragment_to_authScreenFragment)
            }
        }
    }
}