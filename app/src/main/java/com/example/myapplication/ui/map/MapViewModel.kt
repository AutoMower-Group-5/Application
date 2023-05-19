package com.example.myapplication.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.APIMower
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val positionsLiveData = MutableLiveData<FloatArray>()
    private val apiMower = APIMower()

    init {
        viewModelScope.launch {
            val pathData = apiMower.getArrayPath()
            if (pathData != null) {
                // If the pathData is a list of Pair<Double, Double>
                val floatArray = pathData.flatMap { listOf(it.first.toFloat(), it.second.toFloat()) }.toFloatArray()
                setPositionData(floatArray)
            }
        }
    }

    fun getPositionsLiveData(): LiveData<FloatArray> {
        return positionsLiveData
    }

    private fun setPositionData(positionData: FloatArray) {
        positionsLiveData.value = positionData
    }
}


