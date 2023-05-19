package com.example.myapplication.ui.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.myapplication.R

class MapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.myPathColor)
        strokeWidth = resources.getDimension(R.dimen.myPathWidth)
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    // Initialize the paint for the arrow
    private val arrowPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.green) // Change the color here
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Initialize the path for the arrow
    private val arrowPath = Path()

    private var positions: FloatArray? = null

    private val observer = Observer<FloatArray> { positions ->
        Log.d("MapView", "Setting positions: ${positions.joinToString()}")
        this.positions = positions
        invalidate()
    }

    fun bindViewModel(viewModel: MapViewModel) {
        viewModel.getPositionsLiveData().observeForever(observer)
    }

    fun unbindViewModel(viewModel: MapViewModel) {
        viewModel.getPositionsLiveData().removeObserver(observer)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        positions?.let {
            val scaleFactor = 100 // adjust this value according to your needs

            canvas.save()

            canvas.scale(1f, -1f, width / 2f, height / 2f)

            for (i in 0 until it.size - 2 step 2) {
                val startX = it[i] * scaleFactor
                val startY = it[i + 1] * scaleFactor
                val stopX = it[i + 2] * scaleFactor
                val stopY = it[i + 3] * scaleFactor
                Log.d("MapView", "Drawing line from ($startX, $startY) to ($stopX, $stopY)")
                canvas.drawLine(startX, startY, stopX, stopY, paint)
            }

            // Draw an arrow at the last position.
            val lastX = it[it.size - 2] * scaleFactor
            val lastY = it[it.size - 1] * scaleFactor
            arrowPath.reset()
            arrowPath.moveTo(lastX, lastY - 10) // 10 is the half size of the arrow, adjust as needed
            arrowPath.lineTo(lastX - 10, lastY + 10)
            arrowPath.lineTo(lastX + 10, lastY + 10)
            arrowPath.close()
            canvas.drawPath(arrowPath, arrowPaint)

            canvas.restore()
        }
    }
}
