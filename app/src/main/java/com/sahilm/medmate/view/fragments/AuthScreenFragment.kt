package com.sahilm.medmate.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.rpc.context.AttributeContext.Auth
import com.sahilm.medmate.R
import com.sahilm.medmate.auth.AuthClient
import com.sahilm.medmate.databinding.FragmentAuthScreenBinding
import kotlinx.coroutines.launch

class AuthScreenFragment : Fragment() {

    private var _binding: FragmentAuthScreenBinding? = null
    private val binding get() = _binding!!
    lateinit var authClient: AuthClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAuthScreenBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authClient = AuthClient(requireContext())

        binding.llBtnSignIn.setOnClickListener {
            lifecycleScope.launch {
                authClient.signIn()
            }
        }
    }
}