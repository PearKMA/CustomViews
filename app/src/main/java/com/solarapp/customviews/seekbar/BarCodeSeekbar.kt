package com.solarapp.customviews.seekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.solarapp.customviews.R
import com.solarapp.customviews.utils.DimensionUtils
import kotlin.math.abs
import kotlin.math.min

class BarCodeSeekbar : View {
    companion object {
        private const val DEFAULT_WIDTH = 360
        private const val SWIPE_MIN_DISTANCE = 30
        private const val SWIPE_THRESHOLD_VELOCITY = 200
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    public interface OnScrollListener {
        fun onStartScroll(seekbar: BarCodeSeekbar, value: Int)
        fun onScroll(seekbar: BarCodeSeekbar, value: Int, b: Boolean)
        fun onFling(seekbar: BarCodeSeekbar, value: Int)
        fun onScrollFinished(seekbar: BarCodeSeekbar)
    }

    private var mWidth = 0F
    private var mHeight = 0F
    private var space = 0F
    private val padding = 5
    private var mIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val defaultColor = Color.parseColor("#626260")
    private var mBigIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mSmallIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mMaxCount: Int = 100
    private var mListener: OnScrollListener? = null
    private var mDetector: GestureDetectorCompat
    private var mTickLeftValue = 0
    private var mTickRightValue = 50
    private var tickPosition = 0
    private var isTouched = false
    fun testNext() {
        if (mTickLeftValue > 100) {
            mTickLeftValue = 0
            mTickRightValue = 50
        }
        if (tickPosition > 100)
            tickPosition = 0

        ++tickPosition
        ++mTickLeftValue
        ++mTickRightValue
        Log.d("BarCodeSeekbar", "testNext: $tickPosition")
        invalidate()
    }

    fun testSub() {
        if (mTickLeftValue < 0) {
            mTickLeftValue = 50
            mTickRightValue = 100
        }
        if (tickPosition <= 0)
            tickPosition = 100
        --tickPosition
        --mTickLeftValue
        --mTickRightValue
        Log.d("BarCodeSeekbar", "testSub: $tickPosition")
        invalidate()
    }

    init {
        mDetector = GestureDetectorCompat(context, GestureSeekbar())
        setBackgroundResource(R.drawable.bg_indicator_2x)
        //
        mIndicatorPaint.color = Color.BLACK
        mIndicatorPaint.style = Paint.Style.STROKE
        mIndicatorPaint.strokeWidth = DimensionUtils.dpToPixels(context, 4)


        mBigIndicatorPaint.color = Color.WHITE
        mBigIndicatorPaint.style = Paint.Style.STROKE
        mBigIndicatorPaint.strokeWidth = DimensionUtils.dpToPixels(context, 2)

        mSmallIndicatorPaint.color = Color.WHITE
        mSmallIndicatorPaint.style = Paint.Style.STROKE
        mSmallIndicatorPaint.alpha = 200
        mSmallIndicatorPaint.strokeWidth = DimensionUtils.dpToPixels(context, 1)


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w.toFloat()
        mHeight = mWidth / 6
        space = mWidth / 60
    }

    public fun setOnScrollListener(listener: OnScrollListener) {
        mListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val resolveWidthSpec = resolveMeasureSpec(widthMeasureSpec, DEFAULT_WIDTH)
//        val resolveHeightSpec = resolveMeasureSpec(widthMeasureSpec/6, DEFAULT_HEIGHT)
//        super.onMeasure(resolveWidthSpec, resolveHeightSpec)
        val w = MeasureSpec.getSize(resolveMeasureSpec(widthMeasureSpec))
        setMeasuredDimension(w, w / 6)
    }

    private fun resolveMeasureSpec(measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        if (mode == MeasureSpec.EXACTLY)
            return measureSpec
        var pixelDefault = DimensionUtils.dpToPixels(context, DEFAULT_WIDTH).toInt()
        if (mode == MeasureSpec.AT_MOST)
            pixelDefault = min(pixelDefault, MeasureSpec.getSize(measureSpec))
        return MeasureSpec.makeMeasureSpec(pixelDefault, MeasureSpec.EXACTLY)
    }

    public fun setMax(max: Int) {
        mMaxCount = max
        invalidate()
    }

    public fun setProgress(progress: Int) {
        tickPosition = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw center indicator
        val centerX = mWidth / 2
        val space6dp = mHeight * (DimensionUtils.dpToPixels(context, 6) / mHeight)
        canvas?.drawLine(
            centerX,
            space6dp,
            centerX,
            mHeight - space6dp,
            mIndicatorPaint
        )
        val space14dp = mHeight * (DimensionUtils.dpToPixels(context, 14) / mHeight)
        val center = mTickRightValue - mTickLeftValue
        for (i in mTickLeftValue..mTickRightValue) {

            if (tickPosition in 0..25 && i == ((mTickRightValue - mTickLeftValue) / 2 + tickPosition)) {
                mBigIndicatorPaint.color = defaultColor
            } else if (tickPosition in 75..100 && i == (tickPosition - 75 + mTickLeftValue)) {
                mBigIndicatorPaint.color = defaultColor
            }

            when {
                i in (15 + mTickLeftValue)..(mTickLeftValue + 35) -> {
                    defaultAlphaIndicator()
                }
                i < (15 + mTickLeftValue) -> {
                    mBigIndicatorPaint.alpha = (i + 1 - mTickLeftValue) * 10
                    mSmallIndicatorPaint.alpha = (i + 1 - mTickLeftValue) * 10
                }
                else -> {
                    mBigIndicatorPaint.alpha = (mTickRightValue + 1 - i) * 10
                    mSmallIndicatorPaint.alpha = (mTickRightValue + 1 - i) * 10
                }
            }

            canvas?.drawLine(
                (padding + i - mTickLeftValue) * space,
                space14dp,
                (padding + i - mTickLeftValue) * space,
                mHeight - space14dp,
                when (i % 5) {
                    0 -> mBigIndicatorPaint
                    else -> mSmallIndicatorPaint
                }
            )
            mBigIndicatorPaint.color = Color.WHITE
        }

    }


    private fun defaultAlphaIndicator() {
        mBigIndicatorPaint.alpha = 255
        mSmallIndicatorPaint.alpha = 200
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouched = false
                mListener?.onScrollFinished(this)
                Log.d("GestureSeekbar", "onTouchEvent: finish")
            }
            else -> {
                mDetector.onTouchEvent(event)
            }
        }
        if (mDetector.onTouchEvent(event))
            return false
        return true
    }

    private inner class GestureSeekbar : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            isTouched = true
            mListener?.onStartScroll(this@BarCodeSeekbar, tickPosition)
            Log.d("GestureSeekbar", "onDown: ")
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (isTouched) {
                if (e1!!.x - e2!!.x > SWIPE_MIN_DISTANCE) {
                    testSub()
                    Log.d("GestureSeekbar", "onScroll: right to left")
                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE) {
                    testNext()
                    Log.d("GestureSeekbar", "onScroll: left to right")
                }
            }
            return false
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1!!.x - e2!!.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right to left
                Log.d("GestureSeekbar", "onFling: right to left")
            } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d("GestureSeekbar", "onFling: left to right")
            }


            return true
        }
    }
}