package com.example.myapplication.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import kotlinx.coroutines.GlobalScope

class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var mapViewModel: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = rootView.findViewById(R.id.MapView)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        mapView.bindViewModel(mapViewModel)

        mapViewModel.getArrayPath(GlobalScope, activity!!)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        mapViewModel.getArrayPath(GlobalScope, activity!!)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mapView.unbindViewModel(mapViewModel)
    }
}
