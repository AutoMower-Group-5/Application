@file:OptIn(DelicateCoroutinesApi::class)

package com.example.myapplication.ui.remote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentRemoteBinding
import java.util.*
import android.annotation.SuppressLint
import android.view.MotionEvent
import com.example.myapplication.APIMower
import com.example.myapplication.BluetoothService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class RemoteFragment : Fragment() {

    private var _binding: FragmentRemoteBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemoteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val bluetoothService = BluetoothService().getInstance()

        binding.findBluetooth.setOnClickListener { (activity as MainActivity).ensureDiscoverable() }

        binding.toggleMode.setOnClickListener {
            if ((activity as MainActivity).automatic) {
                bluetoothService!!.sendBluetoothCommand("M:M")
                binding.forward.isEnabled = true
                binding.backward.isEnabled = true
                binding.left.isEnabled = true
                binding.right.isEnabled = true
            } else {
                bluetoothService!!.sendBluetoothCommand("M:A")
                binding.forward.isEnabled = false
                binding.backward.isEnabled = false
                binding.left.isEnabled = false
                binding.right.isEnabled = false
            }
            (activity as MainActivity).automatic = !(activity as MainActivity).automatic
        }
        binding.session.setOnClickListener {
            if (!(activity as MainActivity).session) {
                binding.session.text = "End Session"
                GlobalScope.async {
                    APIMower().startSession()
                }
            } else {
                binding.session.text = "Start Session"
                GlobalScope.async {
                    APIMower().endSession()
                }
            }
            (activity as MainActivity).session = !(activity as MainActivity).session
        }
        binding.forward.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    bluetoothService!!.sendBluetoothCommand("D:W")
                }
                MotionEvent.ACTION_UP -> {
                    bluetoothService!!.sendBluetoothCommand("D:Q")
                }
            }
            true
        }
        binding.backward.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    bluetoothService!!.sendBluetoothCommand("D:S")
                }
                MotionEvent.ACTION_UP -> {
                    bluetoothService!!.sendBluetoothCommand("D:Q")
                }
            }
            true
        }
        binding.left.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    bluetoothService!!.sendBluetoothCommand("D:A")
                }
                MotionEvent.ACTION_UP -> {
                    bluetoothService!!.sendBluetoothCommand("D:Q")
                }
            }
            true
        }
        binding.right.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    bluetoothService!!.sendBluetoothCommand("D:D")
                }
                MotionEvent.ACTION_UP -> {
                    bluetoothService!!.sendBluetoothCommand("D:Q")
                }
            }
            true
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        if ((activity as MainActivity).session) {
            binding.session.text = "End Session"
        }
        if ((activity as MainActivity).automatic) {
            binding.forward.isEnabled = false
            binding.backward.isEnabled = false
            binding.left.isEnabled = false
            binding.right.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
