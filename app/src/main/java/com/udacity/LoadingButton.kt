package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    // Setting custom attributes and getting values
    private val attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
    private val customBackgroundColor = attributes.getColor(
        R.styleable.LoadingButton_backgroundColor,
        ContextCompat.getColor(context, R.color.colorPrimaryDark)
    )
    private val customForegroundColor = attributes.getColor(
        R.styleable.LoadingButton_foregroundColor,
        ContextCompat.getColor(context, R.color.colorPrimary)
    )
    private val customCircleColor = attributes.getColor(
        R.styleable.LoadingButton_circleColor,
        ContextCompat.getColor(context, R.color.colorAccent)
    )
    private val customTextColor = attributes.getColor(
        R.styleable.LoadingButton_textColor,
        ContextCompat.getColor(context, R.color.white)
    )

    private var completed: Float = 0.0f
    private var sweepAngle: Float = 0.0f

    private val valueAnimator = ValueAnimator.ofInt(0, 100).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            completed = animatedFraction
            progressRect.right = widthSize * completed
            sweepAngle = 360.0f * completed
            super.invalidate()
        }
    }

    //    Paint logic
    private var progressRect = RectF(0.0f, 0.0f, 0.0f, 0.0f)
    private var circleRect = RectF(0.0f, 0.0f, 0.0f, 0.0f)

    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = customForegroundColor
    }

    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = customCircleColor
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        color = customTextColor
    }

    private var text = resources.getString(R.string.button_name)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                text = resources.getString(R.string.button_name)
            }
            ButtonState.Loading -> {
                text = resources.getString(R.string.button_loading)
                val textRect = Rect()
                textPaint.getTextBounds(text, 0, text.length, textRect)
                circleRect.set(
                    widthSize / 2.0f + textRect.width() / 2.0f + textRect.height() * 0.5f,
                    heightSize / 2.0f - textRect.height() / 2.0f,
                    widthSize / 2.0f + textRect.width() / 2.0f + textRect.height() * 1.5f,
                    heightSize / 2.0f + textRect.height() / 2.0f
                )
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                text = resources.getString(R.string.button_name)
                valueAnimator.end()
                valueAnimator.setCurrentFraction(0.0f)
            }
        }
    }


    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawColor(customBackgroundColor)
            if (buttonState == ButtonState.Loading) {
                it.drawRect(progressRect, progressPaint)
            }
            it.drawText(
                text,
                widthSize / 2.0f,
                heightSize / 2.0f - ((textPaint.ascent() + textPaint.descent()) / 2f),
                textPaint)
            if (buttonState == ButtonState.Loading) {
                it.drawArc(circleRect, 270.0f, sweepAngle, true, circlePaint)
            }
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
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = w
        heightSize = h

        textPaint.textSize = heightSize / 3.0f

        progressRect.bottom = heightSize.toFloat()
        progressRect.right = textPaint.textSize
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Loading) {
            return false
        }
        return true
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

}