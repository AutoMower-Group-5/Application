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
            result = run("https://intense-stream-40056.herokuapp.com/image/get")
        } catch (err: Error) {
            print("Error when executing get request: " + err.localizedMessage)
        }
        return result
    }

    fun getArrayPath(): ResponseBody? {
        var result: ResponseBody? = null
        try {
            result = run("https://intense-stream-40056.herokuapp.com/path/get")
        } catch (err: Error) {
            print("Error when executing get request: " + err.localizedMessage)
        }
        return result
    }
}