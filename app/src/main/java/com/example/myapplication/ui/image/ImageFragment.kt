package com.example.myapplication.ui.image

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.APIMower
import com.example.myapplication.databinding.FragmentImageBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var listImage: Array<DataImage>? = null
    private var numberImage = 0

    @Serializable
    data class DataImage(val Label: String, val URL: String)

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.previousImage.setOnClickListener { clickPreviousImage() }
        binding.previousImage.visibility = INVISIBLE
        binding.nextImage.visibility = INVISIBLE
        binding.nextImage.setOnClickListener { clickNextImage() }

        GlobalScope.async {
            val result = APIMower().getArrayImages()
            if (result != null) {
                listImage = Json.decodeFromString<Array<DataImage>>(result.string())
            }
            activity!!.runOnUiThread {
                if (listImage!!.size > 1) {
                    binding.nextImage.visibility = VISIBLE
                    downloadImageTest()
                } else if (listImage!!.isEmpty()) {
                    binding.label.text = "No image"
                } else {
                    downloadImageTest()
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickNextImage() {
        numberImage += 1
        println("next: $numberImage")
        if (numberImage == listImage!!.size - 1) {
            binding.nextImage.visibility = INVISIBLE
        }
        binding.previousImage.visibility = VISIBLE
        downloadImageTest()
    }

    private fun clickPreviousImage() {
        numberImage -= 1
        println("previous: $numberImage")
        if (numberImage == 0) {
            binding.previousImage.visibility = INVISIBLE
        }
        binding.nextImage.visibility = VISIBLE
        downloadImageTest()
    }

    private fun downloadImageTest() {
        var error = false

        executor.execute {
            val imageURL = listImage?.get(numberImage)!!.URL

            var image: Bitmap? = null
            try {
                val inImage = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(inImage)

            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
                error = true
            }
            handler.post {
                binding.imageView.setImageBitmap(image)
                binding.label.text =
                    if (error) "Error image" else listImage?.get(numberImage)!!.Label
            }
        }
    }
}
