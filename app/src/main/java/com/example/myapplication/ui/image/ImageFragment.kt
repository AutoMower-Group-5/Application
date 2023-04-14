package com.example.myapplication.ui.image

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
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentImageBinding
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private val listImage = listOf<String>(
        "https://storage.googleapis.com/robot-group5.appspot.com/sample_image.jpg",
        "https://gcdatabase.com/images/characters/goddess_elizabeth/ssrr_portrait.png",
        "https://gcdatabase.com/images/characters/goddess_elizabeth/ssrr_1.png"
    )
    private var numberImage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.previousImage.setOnClickListener { clickPreviousImage() }
        binding.previousImage.visibility = INVISIBLE
        binding.nextImage.setOnClickListener { clickNextImage() }
        downloadImageTest()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickNextImage() {
        numberImage += 1
        print("next: $numberImage")
        if (numberImage == listImage.size - 1) {
            binding.nextImage.visibility = INVISIBLE
        }
        binding.previousImage.visibility = VISIBLE
        downloadImageTest()
    }

    private fun clickPreviousImage() {
        numberImage -= 1
        print("previous: $numberImage")
        if (numberImage == 0) {
            binding.previousImage.visibility = INVISIBLE
        }
        binding.nextImage.visibility = VISIBLE
        downloadImageTest()
    }

    private fun downloadImageTest() {
        executor.execute {
            val imageURL = listImage[numberImage]

            var image: Bitmap? = null
            try {
                val inImage = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(inImage)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            handler.post {
                binding.imageView.setImageBitmap(image)
            }
        }
    }
}