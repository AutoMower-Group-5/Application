package com.example.myapplication

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG = "BluetoothChatFragment"
    private val REQUEST_ENABLE_BT = 1
    private lateinit var binding: ActivityMainBinding

    private val mConnectedDeviceName: String? = null
    private var mConversationArrayAdapter: ArrayAdapter<String>? = null
    private var mOutStringBuffer: StringBuffer? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mService: BluetoothService? = null

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
    }

    @SuppressLint("MissingPermission", "NewApi")
    override fun onStart() {
        super.onStart()
        if (mBluetoothAdapter == null) {
            return
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, 3)
        } else if (mService == null) {
            Permission().checkPermissions(this, applicationContext)
            mOutStringBuffer = StringBuffer()
        }
    }

    @SuppressLint("MissingPermission")
    internal fun ensureDiscoverable() {
//        if (mBluetoothAdapter!!.scanMode !=
//            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
//        ) {
//            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//            startActivityForResult(discoverableIntent, 1)
//        }
        val requestCode = 1;
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
        startActivityForResult(discoverableIntent, requestCode)
    }

    internal fun sendMessage(message: String) {
        // Check that we're actually connected before trying anything
//        if (mService.getState() !== BluetoothChatService.STATE_CONNECTED) {
//            return
//        }

        // Check that there's actually something to send
        if (message.isNotEmpty()) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mService?.write(send)

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer!!.setLength(0)
//            mOutEditText.setText(mOutStringBuffer)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mService!!.stop()
    }
}