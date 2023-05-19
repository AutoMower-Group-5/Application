package com.example.myapplication.ui.map

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
                val floatArray = pathData.flatMap { listOf(it.first, it.second) }.toFloatArray()
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
