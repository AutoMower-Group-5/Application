@file:OptIn(DelicateCoroutinesApi::class)

package com.example.myapplication.ui.remote

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentRemoteBinding
import java.io.IOException
import java.io.OutputStream
import java.util.*
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat
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

        var automaticMode = false
        var sessionActive = false
        binding.findBluetooth.setOnClickListener { (activity as MainActivity).ensureDiscoverable() }

        binding.toggleMode.setOnClickListener {
            if (automaticMode) {
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
            automaticMode = !automaticMode
        }
        binding.session.setOnClickListener {
            if (sessionActive) {
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
            sessionActive = !sessionActive
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
