package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.pm.PackageManager
import android.util.Log
import java.io.IOException
import java.util.*

class BluetoothService {
    @Volatile
    private var INSTANCE: BluetoothService? = null
    lateinit var bluetoothAdapter: BluetoothAdapter

    var bluetoothSocket: BluetoothSocket? = null
    val BLUETOOTH_PERMISSION_REQUEST_CODE = 1

    private val bluetoothDeviceAddress = "E4:5F:01:92:B0:3F"

    fun getInstance(): BluetoothService? {
        if (INSTANCE == null) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = BluetoothService()
                }
            }
        }
        return INSTANCE
    }

    fun disconnectDevice() {
        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
        }
        bluetoothSocket = null
    }

    fun connectToDevice() {
        println("----------------------")
        try {
            val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress)
            bluetoothSocket =
                device?.createRfcommSocketToServiceRecord(UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ab"))
            bluetoothSocket?.connect()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println("----------------------")
    }

    fun sendBluetoothCommand(command: String) {
        println("Send: $command ?")
        try {
            bluetoothSocket?.outputStream?.write(command.toByteArray())
            bluetoothSocket?.outputStream?.flush()
            Log.d(ContentValues.TAG, "Command sent successfully: $command")
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error sending command: $command", e)
        }
    }
}