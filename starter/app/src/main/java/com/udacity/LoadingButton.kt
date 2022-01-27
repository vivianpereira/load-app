package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f
    private var textSize: Float = resources.getDimension(R.dimen.default_text_size)

    private var buttonTitle: String = ""
    private var buttonColor = 0
    private var loadingButtonColor = 0
    private var textColor = 0
    private var loadingCircleColor = 0

    private var valueAnimator = ValueAnimator()
    private var progressButtonWidth = 0.0f
    private var progressCircle = 0f
    private var circleXOffset = textSize / 2

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = LoadingButton::textSize.invoke(this@LoadingButton)
        typeface = Typeface.DEFAULT
    }
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, _ ->
        when (buttonState) {
            ButtonState.Loading -> startAnimator()
            ButtonState.Completed -> completeDownload()
        }
    }

    // initialize
    init {
        buttonTitle = resources.getString(R.string.button_download)
        context.withStyledAttributes(set = attrs, attrs = R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            loadingButtonColor = getColor(R.styleable.LoadingButton_loadingButtonColor, 0)
            textColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            loadingCircleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButtonBackground(canvas)
        drawButtonProgress(canvas)
        drawButtonTitle(canvas)
        drawCircleProgress(canvas)
    }

    private fun startAnimator() {
        buttonTitle = resources.getString(R.string.button_loading)
        valueAnimator.setFloatValues(0.0f, width.toFloat())
        valueAnimator.duration = 3000
        valueAnimator.addUpdateListener {
            progressButtonWidth = it.animatedValue as Float
            progressCircle = (progressButtonWidth * 360f) / measuredWidth
            if (progressButtonWidth < measuredWidth.toFloat()) {
                invalidate()
            } else {
                buttonState = ButtonState.Completed
            }
        }
        valueAnimator.start()
    }
    private fun completeDownload() {
        valueAnimator.cancel()
        buttonTitle = resources.getString(R.string.button_download)
        progressCircle = 0f
        progressButtonWidth = 0f
        invalidate()
    }

    private fun drawButtonBackground(canvas: Canvas) {
        paint.color = buttonColor
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
    }

    private fun drawButtonProgress(canvas: Canvas) {
        paint.color = loadingButtonColor
        canvas.drawRect(0f, 0f, progressButtonWidth, heightSize.toFloat(), paint)
    }

    private fun drawCircleProgress(canvas: Canvas) {
        with(canvas) {
            save()
            translate(
                widthSize / 2 + textWidth / 2 + circleXOffset,
                heightSize / 2 - textSize / 2
            )
            paint.color = loadingCircleColor
            drawArc(RectF(0f, 0f, textSize, textSize), 0F, progressCircle, true, paint)
            restore()
        }
    }

    private fun drawButtonTitle(canvas: Canvas?) {
        paint.color = Color.WHITE
        textWidth = paint.measureText(buttonTitle)
        canvas?.drawText(
            buttonTitle,
            (widthSize / 2).toFloat(),
            ((heightSize + 30) / 2).toFloat(),
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val width = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val height = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed) {
            buttonState = ButtonState.Loading
        }
        startAnimator()
        return true
    }
}