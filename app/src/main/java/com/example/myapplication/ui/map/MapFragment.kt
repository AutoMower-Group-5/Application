package com.example.myapplication.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.APIMower
import com.example.myapplication.databinding.FragmentMapBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Create an instance of the APIMower class
        val apiMower = APIMower()

        // Call the getArrayPath() function using a coroutine
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                apiMower.getArrayPath()
            }

            // Convert the response to a string and display it in a text view
            response?.let {
                val pathData = it.string()
                val textView: TextView = binding.textMap
                textView.text = pathData
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}