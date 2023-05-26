package com.example.myapplication.ui.map

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.APIMower
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONArray
import kotlinx.coroutines.launch


class MapViewModel : ViewModel() {
    private val positionsLiveData = MutableLiveData<FloatArray>()
    private val collisionPointsLiveData = MutableLiveData<List<Pair<Float, Float>>>()

    private val apiMower = APIMower()

    fun getArrayPath(scope: CoroutineScope, activity: Activity) {
        scope.launch {
            val responseBody = apiMower.getArrayPath()
            val jsonData = responseBody?.string()
            val jsonArray = JSONArray(jsonData)

            val pathData = mutableListOf<Pair<Float, Float>>()
            val collisionPoints = mutableListOf<Pair<Float, Float>>() // List to store collision points

            for (i in 0 until jsonArray.length()) {
                val coordObject = jsonArray.getJSONObject(i)
                val x = coordObject.getDouble("x").toFloat()
                val y = coordObject.getDouble("y").toFloat()
                pathData.add(Pair(x, y))

                // Check if the current point is a collision point and add it to the list
                if (coordObject.optBoolean("collision")) {
                    collisionPoints.add(Pair(x, y))
                }
            }
            getCollisionPath(scope) // Fetch collision points

            collisionPointsLiveData.postValue(collisionPoints) // Update collisionPointsLiveData with the list of collision points

            // If the pathData is a list of Pair<Double, Double>
            val floatArray = pathData.flatMap { listOf(it.first, it.second) }.toFloatArray()
            activity.runOnUiThread {
                setPositionData(floatArray)
            }
        }
    }

    fun getCollisionPath(scope: CoroutineScope) {
        scope.launch {
            val responseBody = apiMower.getCollisionPath()
            val jsonData = responseBody?.string()
            val jsonArray = JSONArray(jsonData)

            val collisionPoints = mutableListOf<Pair<Float, Float>>() // List to store collision points

            for (i in 0 until jsonArray.length()) {
                val coordObject = jsonArray.getJSONObject(i)
                val x = coordObject.getDouble("x").toFloat()
                val y = coordObject.getDouble("y").toFloat()
                collisionPoints.add(Pair(x, y))
            }

            collisionPointsLiveData.postValue(collisionPoints) // Update collisionPointsLiveData with the list of collision points
        }
    }

    fun getPositionsLiveData(): LiveData<FloatArray> {
        return positionsLiveData
    }

    fun getCollisionPointsLiveData(): LiveData<List<Pair<Float, Float>>> {
        return collisionPointsLiveData
    }

    private fun setPositionData(positionData: FloatArray) {
        positionsLiveData.value = positionData
    }
}

