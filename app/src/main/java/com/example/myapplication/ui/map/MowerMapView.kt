package com.example.myapplication.ui.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class MowerMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mowerPositions = emptyList<Pair<Float, Float>>()

    private val mowerPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    fun setMowerPositions(positions: List<Pair<Float, Float>>) {
        this.mowerPositions = positions
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        // Draw each segment of the mower's path
        for (i in 0 until mowerPositions.size - 1) {
            val start = mowerPositions[i]
            val end = mowerPositions[i + 1]
            canvas.drawLine(start.first, start.second, end.first, end.second, mowerPaint)
        }
    }
}
