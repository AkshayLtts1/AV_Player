package com.akshay.playerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.akshay.playerapp.databinding.FragmentAudioBinding

class AudioFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_audio, container, false)
        val binding = FragmentAudioBinding.bind(view)

        binding.audioRV.setHasFixedSize(true)
        binding.audioRV.setItemViewCacheSize(10)
        binding.audioRV.layoutManager = LinearLayoutManager(requireContext())
        binding.audioRV.adapter = AudioAdapter(requireContext(), MainActivity.audioList)
        binding.totalAudios.text = "Total Audios: ${MainActivity.audioList.size}"
        return view
    }
}