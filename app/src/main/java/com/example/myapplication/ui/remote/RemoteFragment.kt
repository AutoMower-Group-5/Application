package com.example.myapplication.ui.remote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentRemoteBinding

class RemoteFragment : Fragment() {

    private var _binding: FragmentRemoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val remoteViewModel =
            ViewModelProvider(this)[RemoteViewModel::class.java]

        _binding = FragmentRemoteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (remoteViewModel.automaticMode) {
            binding.forward.isEnabled = false
            binding.backward.isEnabled = false
            binding.left.isEnabled = false
            binding.right.isEnabled = false
        }

        binding.findBluetooth.setOnClickListener { (activity as MainActivity).ensureDiscoverable() }

        binding.toggleMode.setOnClickListener {
            remoteViewModel.automaticMode = !remoteViewModel.automaticMode
            if (remoteViewModel.automaticMode) {
                binding.forward.isEnabled = false
                binding.backward.isEnabled = false
                binding.left.isEnabled = false
                binding.right.isEnabled = false
            } else {
                binding.forward.isEnabled = true
                binding.backward.isEnabled = true
                binding.left.isEnabled = true
                binding.right.isEnabled = true
            }
        }

        binding.forward.setOnClickListener { print("forward") }
        binding.backward.setOnClickListener { print("backward") }
        binding.left.setOnClickListener { print("left") }
        binding.right.setOnClickListener { print("right") }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
