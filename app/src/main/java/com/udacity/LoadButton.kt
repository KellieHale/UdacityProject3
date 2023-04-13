package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.properties.Delegates

class LoadButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val ANIMATION_DURATION = 3000L
        private const val ANIMATION_START_VALUE = 0.0f
        private const val ANIMATION_END_VALUE = 1.0f

        private const val CIRCLE_START_ANGLE = 0.0f
        private const val CIRCLE_SWEEP_ANGLE = 360.0f
    }


    var text: String = ""

    private var valueAnimator = ValueAnimator()

    private var loadingProgress = 0.0F

    //-- Attributes from attrs.xml
    private var loadingColor: Int = 0
    private var loadingColorCompleted: Int = 0
    var loadingCompletedText: String? = ""
    var loadingText: String? = null

    //-- View Attributes
    private var viewWidth = 0
    private var viewHeight = 0
    private var circleRadius = context.resources.getDimension(R.dimen.radius)

    var state: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                text = loadingText ?: ""
                valueAnimator = ValueAnimator.ofFloat(
                    ANIMATION_START_VALUE,
                    ANIMATION_END_VALUE
                ).apply {
                    duration = ANIMATION_DURATION
                    repeatCount = ValueAnimator.INFINITE
                    addUpdateListener {
                        loadingProgress = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                valueAnimator = ValueAnimator.ofFloat(loadingProgress, ANIMATION_END_VALUE).apply {
                    duration = ANIMATION_DURATION
                    addUpdateListener {
                        loadingProgress = it.animatedValue as Float
                        invalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            loadingCompletedText?.let { completedText ->
                                text = completedText
                            } ?: run { text = ""}
                            loadingProgress = 0.0F
                            invalidate()
                        }
                    })
                    start()
                }
            }
        }
        invalidate()
    }

    /**
     * Creates a Paint object with Anti-Aliasing on to allow for a clean circular path
     */
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorAccent)
    }

    private val colorPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.white)
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.default_text_size)
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadButton, 0, 0).apply {
            loadingColor = getColor(R.styleable.LoadButton_loadingColorStart, 0)
            loadingColorCompleted = getColor(R.styleable.LoadButton_loadingColorCompleted, 0)
            loadingText = getString(R.styleable.LoadButton_loadingTextStarted)
            loadingCompletedText = getString(R.styleable.LoadButton_loadingTextCompleted)
        }
        loadingProgress = ANIMATION_START_VALUE
        text = loadingCompletedText ?: ""
    }

    /**
     * Draws the color on the Canvas from a 0, 0, and changes the Paint color to the loading color
     * based on the progress of animation from 0 to 3 seconds. This animation is added from the left
     * view bound of 0, to the right (width of the total view * loading percentage)
     *
     * @param canvas
     */
    private fun drawColor(canvas: Canvas) {
        canvas.drawColor(loadingColorCompleted)

        colorPaint.color = loadingColor
        canvas.drawRect(
            0F,
            0F,
            viewWidth.toFloat() * loadingProgress,
            viewHeight.toFloat(),
            colorPaint
        )
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(
            text,
            viewWidth.toFloat() / 2,
            viewHeight.toFloat() / 2,
            textPaint
        )
    }

    private fun drawCircleArc(canvas: Canvas) {
        val diameter = circleRadius * 2
        val left = 0F + diameter
        val top = viewHeight.toFloat() / 2 - circleRadius
        canvas.drawArc(
            left,
            top,
            diameter + left,
            diameter + top,
            CIRCLE_START_ANGLE,
            CIRCLE_SWEEP_ANGLE * loadingProgress,
            true,
            circlePaint
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawColor(it)
            drawText(it)
            drawCircleArc(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        viewWidth = measureDimension(desiredWidth, widthMeasureSpec)
        viewHeight = measureDimension(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(viewWidth, viewHeight)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        if (result < desiredSize) {
            Log.e("LoadButton", "The view is too small, the content might get cut")
        }
        return result
    }
}