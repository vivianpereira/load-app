package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var buttonColor = 0
    private var loadingColor = 0
    private var circleColor = 0
    private var textColor = 0

    private var valueAnimator = ValueAnimator()
    private var widthAnimator = 0.0f

    // set attributes of paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER // button text alignment
        textSize = 50.0f //button text size
        typeface = Typeface.create("", Typeface.BOLD) // button text's font style
    }

    // observes the state of button
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (buttonState) {
            ButtonState.Loading -> startAnimator()
            ButtonState.Completed -> completeDownload()
        }
    }

    private fun startAnimator() {
        valueAnimator.duration = 3000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.setFloatValues(0.0f, width.toFloat())
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener {
            widthAnimator = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.start()
    }

    // call after downloading is completed
    private fun completeDownload() {
        valueAnimator.cancel()
        invalidate()
    }

    // initialize
    init {
        context.withStyledAttributes(set = attrs, attrs = R.styleable.LoadingButton){
            buttonColor = getColor(R.styleable.LoadingButton_ButtonColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingButtonColor, 0)
            textColor = getColor(R.styleable.LoadingButton_loadingTextColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        startAnimator()

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = buttonColor
        // draw custom button
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // to show rectangular progress on custom button while file is downloading
        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#004349")
            canvas.drawRect(
                0f, 0f,
                (width * (widthSize / 100)).toFloat(), height.toFloat(), paint
            )
        }

        // check the button state
        val buttonText = if (buttonState == ButtonState.Loading)
            resources.getString(R.string.button_loading)  // We are loading as button text
        else resources.getString(R.string.button_download)// download as button text

        // write the text on custom button
        paint.color = textColor
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
    }
}