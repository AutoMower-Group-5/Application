package com.example.myapplication.ui.map

import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.APIMower
import com.example.myapplication.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray

class MapViewModel : ViewModel() {
    private val positionsLiveData = MutableLiveData<FloatArray>()
    private val apiMower = APIMower()

    init {
    }

    fun getArrayPath(scope: CoroutineScope, activity: Activity) {
        scope.async{
            val responseBody = apiMower.getArrayPath()
            val jsonData = responseBody?.string()
            val jsonArray = JSONArray(jsonData)

            val pathData = mutableListOf<Pair<Float, Float>>()
            for (i in 0 until jsonArray.length()) {
                val coordObject = jsonArray.getJSONObject(i)
                val x = coordObject.getDouble("x").toFloat()
                val y = coordObject.getDouble("y").toFloat()
                pathData.add(Pair(x, y))
            }

            // If the pathData is a list of Pair<Double, Double>
            val floatArray = pathData.flatMap { listOf(it.first, it.second) }.toFloatArray()
            activity.runOnUiThread{setPositionData(floatArray)}
        }
    }
    fun getPositionsLiveData(): LiveData<FloatArray> {
        return positionsLiveData
    }

    private fun setPositionData(positionData: FloatArray) {
        positionsLiveData.value = positionData
    }
}


