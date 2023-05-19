package com.example.myapplication

import okhttp3.*
import java.io.IOException
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONArray

class APIMower {

    private val client = OkHttpClient()

    private fun run(url: String): ResponseBody? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body
    }

    fun getArrayImages(): ResponseBody? {
        var result: ResponseBody? = null
        try {
//            result = run("https://intense-stream-40056.herokuapp.com/image/get")
            result = run("https://intense-stream-40056.herokuapp.com/image/session/get")
            println(result)
        } catch (err: Error) {
            println("Error when executing get request: " + err.localizedMessage)
        }
        return result
    }

    suspend fun getArrayPath(): List<Pair<Float, Float>>? = withContext(Dispatchers.IO) {
        try {
            val responseBody = run("https://intense-stream-40056.herokuapp.com/path/session/get")
            val jsonData = responseBody?.string()
            val jsonArray = JSONArray(jsonData)

            val pathData = mutableListOf<Pair<Float, Float>>()
            for (i in 0 until jsonArray.length()) {
                val coordArray = jsonArray.getJSONArray(i)
                pathData.add(Pair(coordArray.getDouble(0).toFloat(), coordArray.getDouble(1).toFloat()))
            }

            pathData
        } catch (err: Throwable) {
            println("Error when executing get request: " + err.localizedMessage)
            null
        }
    }


    fun startSession(): ResponseBody? {
        var result: ResponseBody? = null
        try {
            result = run("https://intense-stream-40056.herokuapp.com/session/start")
        } catch (err: Error) {
            println("Error when executing get request: " + err.localizedMessage)
        }
        return result
    }

    fun endSession(): ResponseBody? {
        var result: ResponseBody? = null
        try {
            result = run("https://intense-stream-40056.herokuapp.com/session/end")
        } catch (err: Error) {
            println("Error when executing get request: " + err.localizedMessage)
        }
        return result
    }

    fun isSession(): ResponseBody? {
        var result: ResponseBody? = null
        try {
            result = run("https://intense-stream-40056.herokuapp.com/session/check")
        } catch (err: Error) {
            println("Error when executing get request: " + err.localizedMessage)
        }
        return result
    }


}