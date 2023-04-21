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
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat

class RemoteFragment : Fragment() {

    private var _binding: FragmentRemoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket? = null

    companion object {
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1
    }

    private val bluetoothDeviceAddress = "E4:5F:01:92:B0:3F"

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
                val automaticModeCommand = "M:A"
                bluetoothSocket?.outputStream?.write(automaticModeCommand.toByteArray())
                binding.forward.isEnabled = false
                binding.backward.isEnabled = false
                binding.left.isEnabled = false
                binding.right.isEnabled = false
            } else {
                val manualModeCommand = "M:M"
                bluetoothSocket?.outputStream?.write(manualModeCommand.toByteArray())
                binding.forward.isEnabled = true
                binding.backward.isEnabled = true
                binding.left.isEnabled = true
                binding.right.isEnabled = true
            }
        }

        binding.forward.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    sendBluetoothCommand("D:W")
                }
                MotionEvent.ACTION_UP -> {
                    sendBluetoothCommand("S:W")
                }
            }
            true
        }

        binding.backward.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    sendBluetoothCommand("D:S")
                }
                MotionEvent.ACTION_UP -> {
                    sendBluetoothCommand("S:S")
                }
            }
            true
        }

        binding.left.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    sendBluetoothCommand("D:A")
                }
                MotionEvent.ACTION_UP -> {
                    sendBluetoothCommand("S:A")
                }
            }
            true
        }

        binding.right.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    sendBluetoothCommand("D:D")
                }
                MotionEvent.ACTION_UP -> {
                    sendBluetoothCommand("S:D")
                }
            }
            true
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            connectToDevice()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH),
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        }
        return root
    }

    private fun connectToDevice() {
        try {
            val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress)
            bluetoothSocket = device?.createRfcommSocketToServiceRecord(UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ab"))
            bluetoothSocket?.connect()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            connectToDevice()
        }
    }

    private fun sendBluetoothCommand(command: String) {
        try {
            // Put the mower in manual mode

            // Send the desired command
            bluetoothSocket?.outputStream?.write(command.toByteArray())

            // Put the mower back in automatic mode

            // Flush the output stream
            bluetoothSocket?.outputStream?.flush()

            // Print a success message
            Log.d(TAG, "Command sent successfully: $command")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending command: $command", e)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
