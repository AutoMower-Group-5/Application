package com.example.myapplication

import okhttp3.*
import java.io.IOException
import java.net.URL

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

    fun getArrayPath(): ResponseBody? {
        var result: ResponseBody? = null
        try {
            result = run("https://intense-stream-40056.herokuapp.com/path/session/get")
        } catch (err: Error) {
            println("Error when executing get request: " + err.localizedMessage)
        }
        return result
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