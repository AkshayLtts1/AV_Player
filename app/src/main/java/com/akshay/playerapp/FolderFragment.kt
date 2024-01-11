package com.akshay.playerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.akshay.playerapp.databinding.FragmentFolderBinding

class FolderFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_folder, container, false)
        val binding = FragmentFolderBinding.bind(view)
        binding.FoldersFV.setHasFixedSize(true)
        binding.FoldersFV.setItemViewCacheSize(10)
        binding.FoldersFV.layoutManager = LinearLayoutManager(requireContext())
        binding.FoldersFV.adapter = FoldersAdapter(requireContext(), MainActivity.folderList)
        binding.totalFolders.text = "Total folders: ${MainActivity.folderList.size}"
        return view
    }
}