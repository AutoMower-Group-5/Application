package com.example.myapplication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

@Suppress("DEPRECATION")
@SuppressLint("MissingPermission")
class BluetoothService(
// handler that gets info from Bluetooth service
    private val handler: Handler
) {

    private val mAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mHandler: Handler = handler
//    private var mAcceptThread: BluetoothService.AcceptThread? = null
    private var mConnectThread: BluetoothService.ConnectThread? = null
    private var mConnectedThread: BluetoothService.ConnectedThread? = null

    @get:Synchronized
    public var actualState: Int
        private set
    private var mNewState: Int

    init {
        actualState = STATE_NONE
        mNewState = actualState
    }

    @Synchronized
    fun start() {
        Log.d(TAG, "start")

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }

        // Start the thread to listen on a BluetoothServerSocket
//        if (mAcceptThread == null) {
//            mAcceptThread = AcceptThread()
//            mAcceptThread!!.start()
//        }
    }

    @Synchronized
    fun connect(device: BluetoothDevice, secure: Boolean) {
        Log.d(TAG, "connect to: $device")

        // Cancel any thread attempting to make a connection
        if (actualState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread!!.cancel()
                mConnectThread = null
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }

        // Start the thread to connect with the given device
        mConnectThread = ConnectThread(device)
        mConnectThread!!.start()
    }

    fun connected(socket: BluetoothSocket?, device: BluetoothDevice) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }

        // Cancel the accept thread because we only want to connect to one device
//        if (mAcceptThread != null) {
//            mAcceptThread!!.cancel()
//            mAcceptThread = null
//        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = ConnectedThread(socket!!)
        mConnectedThread!!.start()

        // Send the name of the connected device back to the UI Activity
        val msg = mHandler.obtainMessage(4)
        val bundle = Bundle()
        bundle.putString("Test name", device.name)
        msg.data = bundle
        mHandler.sendMessage(msg)
    }

    @Synchronized
    fun stop() {
        Log.d(TAG, "stop")
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
//        if (mAcceptThread != null) {
//            mAcceptThread!!.cancel()
//            mAcceptThread = null
//        }
        actualState = STATE_NONE
    }

    fun write(out: ByteArray?) {
        // Create temporary object
        var r: BluetoothService.ConnectedThread?
        // Synchronize a copy of the ConnectedThread
        synchronized(this) {
            if (actualState != STATE_CONNECTED) return
            r = mConnectedThread
        }
        // Perform the write unsynchronized
        out?.let { r!!.write(it) }
    }

//    private inner class AcceptThread : Thread() {
//
//        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            mAdapter.listenUsingInsecureRfcommWithServiceRecord("AppMower", UUIDMe)
//        }
//
//        override fun run() {
//            // Keep listening until exception occurs or a socket is returned.
//            while (actualState != STATE_CONNECTED) {
//                val socket: BluetoothSocket? = try {
//                    mmServerSocket?.accept()
//                } catch (e: IOException) {
//                    Log.e(TAG, "Socket's accept() method failed", e)
//                    null
//                }
//                // If a connection was accepted
//                if (socket != null) {
//                    synchronized(this@BluetoothService) {
//                        when (actualState) {
//                            STATE_LISTEN, STATE_CONNECTING ->                                 // Situation normal. Start the connected thread.
//                                connected(
//                                    socket, socket.remoteDevice, mSocketType
//                                )
//                            STATE_NONE, STATE_CONNECTED ->                                 // Either not ready or already connected. Terminate new socket.
//                                try {
//                                    socket.close()
//                                } catch (e: IOException) {
//                                    Log.e(
//                                        TAG, "Could not close unwanted socket", e
//                                    )
//                                }
//                            else -> {}
//                        }
//                    }
//                }
////                socket?.also {
////                    manageMyConnectedSocket(it)
////                    mmServerSocket?.close()
////                }
//            }
//        }
//
//        // Closes the connect socket and causes the thread to finish.
//        fun cancel() {
//            try {
//                mmServerSocket?.close()
//            } catch (e: IOException) {
//                Log.e(TAG, "Could not close the connect socket", e)
//            }
//        }
//    }

    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUIDMower)
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            mAdapter.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                // Reset the ConnectThread because we're done
                synchronized(this@BluetoothService) { mConnectThread = null }

                // Start the connected thread
                connected(mmSocket, device)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                // Send the obtained bytes to the UI activity.
                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1, mmBuffer
                )
                readMsg.sendToTarget()
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer
            )
            writtenMsg.sendToTarget()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    companion object {
        // Debugging
        private const val TAG = "BluetoothService"

        // Unique UUID for this application
        private val UUIDMower = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ab")
        private val UUIDMe = UUID.fromString("36d5d07d-e285-472d-a9f7-525c20368418")

        // Constants that indicate the current connection actualState
        const val STATE_NONE = 0 // we're doing nothing
        const val STATE_LISTEN = 1 // now listening for incoming connections
        const val STATE_CONNECTING = 2 // now initiating an outgoing connection
        const val STATE_CONNECTED = 3 // now connected to a remote device

        var MESSAGE_STATE_CHANGE = 1
        var MESSAGE_READ = 2
        var MESSAGE_WRITE = 3
        var MESSAGE_DEVICE_NAME = 4
        var MESSAGE_TOAST = 5
    }
}