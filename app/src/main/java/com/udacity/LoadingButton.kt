package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                text = textLoad ?: ""
                valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                    duration = 2000L
                    repeatCount = ValueAnimator.INFINITE
                    addUpdateListener {
                        progress = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                valueAnimator = ValueAnimator.ofFloat(progress, 1F).apply {
                    duration = 2000L
                    addUpdateListener {
                        progress = it.animatedValue as Float
                        invalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            text = textComp ?: ""
                            progress = 0.0F
                            invalidate()
                        }
                    })
                    start()
                }
            }
            else -> Unit
        }
        invalidate()
    }

    private val cirPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorAccent)
    }

    private val tPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.white)
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.default_text_size)
    }

    private val cPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val radius = context.resources.getDimension(R.dimen.radius)
    private var wSize = 0
    private var hSize = 0
    private var colorLoad: Int = 0
    private var colorComp: Int = 0
    private var textLoad: String? = null
    private var textComp: String? = null
    private var text: String = ""
    private var progress = 0.0F

    private var valueAnimator = ValueAnimator()

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            colorLoad = getColor(R.styleable.LoadingButton_cLoading, 0)
            colorComp = getColor(R.styleable.LoadingButton_cCompleted, 0)
            textLoad = getString(R.styleable.LoadingButton_tLoading)
            textComp = getString(R.styleable.LoadingButton_tCompleted)
        }
        text = textComp ?: ""
        progress = 0.0F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {

            canvas.drawColor(colorComp)

            cPaint.color = colorLoad
            canvas.drawRect(
                0F,
                0F,
                wSize.toFloat() * progress,
                hSize.toFloat(),
                cPaint
            )

            val textH = tPaint.descent() - tPaint.ascent()
            val textOffset = textH / 1 - tPaint.descent()
            canvas.drawText(
                text,
                wSize.toFloat() / 2,
                hSize.toFloat() / 2 + textOffset,
                tPaint
            )

            val arcX = 3 * wSize.toFloat() / 4
            val arcY = hSize.toFloat() / 2
            canvas.drawArc(
                arcX,
                arcY - radius,
                radius * 2 + arcX,
                radius + arcY,
                0F,
                360F * progress,
                true,
                cirPaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        wSize = w
        hSize = h
        setMeasuredDimension(w, h)
    }
}