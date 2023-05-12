package com.example.myapplication.ui.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
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

    private var positions: FloatArray? = null

    private val observer = Observer<FloatArray> { positions ->
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
            for (i in 0 until it.size - 2 step 2) {
                val startX = it[i]
                val startY = it[i + 1]
                val stopX = it[i + 2]
                val stopY = it[i + 3]
                canvas.drawLine(startX, startY, stopX, stopY, paint)
            }
        }
    }
}
