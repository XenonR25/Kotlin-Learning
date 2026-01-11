package com.example.assignment1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.assignment1.Context.Service.MusicService
import com.example.assignment1.databinding.FragmentMusicBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MusicFragment : Fragment(R.layout.fragment_music) {
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private var currentSongIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMusicBinding.bind(view)

        binding.btnPlay.setOnClickListener {
            playSelectedSong()
        }

        binding.btnStop.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java).apply {
                action = MusicService.ACTION_STOP
            }
            requireActivity().startService(intent)
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java).apply {
                action = MusicService.ACTION_NEXT
            }
            requireActivity().startService(intent)
        }

        binding.btnPrevious.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java).apply {
                action = MusicService.ACTION_PREVIOUS
            }
            requireActivity().startService(intent)
        }

        // Song selection buttons
        binding.btnSong1.setOnClickListener {
            currentSongIndex = 0
            playSongAtIndex(0)
        }

        binding.btnSong2.setOnClickListener {
            currentSongIndex = 1
            playSongAtIndex(1)
        }

        binding.btnSong3.setOnClickListener {
            currentSongIndex = 2
            playSongAtIndex(2)
        }

        binding.btnCrashTest.setOnClickListener {
            FirebaseCrashlytics.getInstance().log("Test Crash button clicked")
            throw RuntimeException("Test Crash")
        }

        requestNotificationPermission()
    }

    private fun playSelectedSong() {
        playSongAtIndex(currentSongIndex)
    }

    private fun playSongAtIndex(index: Int) {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY
            putExtra(MusicService.EXTRA_SONG_INDEX, index)
        }
        requireActivity().startService(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }
}