package com.solarapp.customviews.fancontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class FanController : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, def: Int) : super(context, attrs, def)


    companion object {
        private const val SELECTION_COUNT = 4
    }

    private var mWidth = 0F
    private var mHeight = 0F
    private var mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mButtonPaint: Paint
    private var mRadius = 0F
    private var mActiveSelection = 0
    //
    private val mTempLabel = StringBuffer(8)
    private var mTempResult = FloatArray(2)

    init {
        mTextPaint.color = Color.BLACK
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = 40f
        mButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mButtonPaint.color = Color.GRAY

        mActiveSelection = 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        mRadius = (min(mWidth, mHeight) / 2 * 0.8).toFloat()

    }

    private fun calculateXYPosition(pos: Int, radius: Float): FloatArray {
        val result = mTempResult
        val startAngle = Math.PI * (9 / 8)
        val angle = startAngle + (pos * (Math.PI / mActiveSelection))
        result[0] = ((radius * cos(angle)) + (mWidth / 2)).toFloat()
        result[1] = ((radius * sin(angle)) + (mHeight / 2)).toFloat()
        return result
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //Khuôn
        canvas?.drawCircle(mWidth / 2, mHeight / 2, mRadius, mButtonPaint)
        //text
        val labelRadius = mRadius + 20
        val label = mTempLabel
        for (i in 0 until SELECTION_COUNT) {
            val xyData = calculateXYPosition(i, labelRadius)
            val x = xyData[0]
            val y = xyData[1]
            label.setLength(0)
            label.append(i)
            canvas?.drawText(label, 0, label.length, x, y, mTextPaint)
        }
        //draw indicator
        val markerRadius = mRadius - 35
        val xyData = calculateXYPosition(mActiveSelection, markerRadius)
        canvas?.drawCircle(xyData[0], xyData[1], 20F, mTextPaint)
    }

}