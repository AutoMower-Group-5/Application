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
            if (it.size < 4) return // Need at least two points to draw a line

            // Find the minimum and maximum values for x and y coordinates
            val minX = it.minOrNull() ?: 0f
            val minY = it.minOrNull() ?: 0f
            val maxX = it.maxOrNull() ?: 0f
            val maxY = it.maxOrNull() ?: 0f

            // Calculate the scale factor based on the maximum difference between min and max values
            val diffX = maxX - minX
            val diffY = maxY - minY
            val scaleFactor = kotlin.math.min(width.toFloat() / diffX, height.toFloat() / diffY)

            canvas.save()

            canvas.scale(1f, -1f, width / 2f, height / 2f)

            for (i in 0 until it.size - 2 step 2) {
                val startX = (it[i] - minX) * scaleFactor
                val startY = (it[i + 1] - minY) * scaleFactor
                val stopX = (it[i + 2] - minX) * scaleFactor
                val stopY = (it[i + 3] - minY) * scaleFactor
                Log.d("MapView", "Drawing line from ($startX, $startY) to ($stopX, $stopY)")
                canvas.drawLine(startX, startY, stopX, stopY, paint)
            }

            // Draw an arrow at the last position.
            val lastX = (it[it.size - 2] - minX) * scaleFactor
            val lastY = (it[it.size - 1] - minY) * scaleFactor
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
