package com.example.myapplication.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {
    private val positionsLiveData = MutableLiveData<FloatArray>()
    fun getPositionsLiveData(): LiveData<FloatArray> {
        return positionsLiveData
    }

    fun setPositionData(positionData: FloatArray) {
        positionsLiveData.value = positionData
    }
}
