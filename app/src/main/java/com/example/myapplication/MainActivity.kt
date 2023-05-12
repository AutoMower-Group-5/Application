package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.remote.RemoteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val bluetoothService = BluetoothService().getInstance()
    var session = false
    var automatic = false
    var connected = false

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_remote, R.id.navigation_map, R.id.navigation_image
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Permission().checkPermissions(this, applicationContext)

        bluetoothService!!.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothService.connectToDevice()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH),
                bluetoothService.BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        }

        GlobalScope.async {
            val result = APIMower().isSession()
            session = result!!.string() != "false"
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    override fun onStart() {
        super.onStart()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == bluetoothService!!.BLUETOOTH_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            bluetoothService.connectToDevice()
//            connected = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothService!!.bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}