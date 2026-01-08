package com.example.assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.assignment1.Context.Service.MusicService
import com.example.assignment1.databinding.FragmentMusicBinding

class MusicFragment : Fragment(R.layout.fragment_music) {
    private var _binding : FragmentMusicBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMusicBinding.bind(view)

        binding.btnPlay.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java)
            requireActivity().startService(intent)
        }

        _binding?.btnStop?.setOnClickListener{
            val intent = Intent(requireContext(), MusicService::class.java)
            requireActivity().stopService(intent)
        }
    }
    //Clean up to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null 
    }
}