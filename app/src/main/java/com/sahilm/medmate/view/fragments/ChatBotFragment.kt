package com.sahilm.medmate.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sahilm.medmate.R
import com.sahilm.medmate.databinding.FragmentChatBotBinding

class ChatBotFragment : Fragment() {

    private var _binding: FragmentChatBotBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentChatBotBinding.inflate(inflater, container, false)
        return (binding.root)
    }
}