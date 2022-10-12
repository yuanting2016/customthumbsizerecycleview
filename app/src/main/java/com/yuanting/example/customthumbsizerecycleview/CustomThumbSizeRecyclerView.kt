package com.yuanting.example.customthumbsizerecycleview

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ScrollingView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * copyright by https://coffeecode97.medium.com/recyclerview-custom-thumb-size-length-136f1836af9c
 * @description: 包含自适应scrollbar的recycleview
 * @author: tingyuan
 * @date: 2022/10/12
 */
class CustomThumbSizeRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), ScrollingView {

    var thumbLength: Int
    private var trackLength = -1
    private var totalLength = -1

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomThumbSizeRecyclerView)
        thumbLength = dpToPx(
            typedArray.getDimension(
                R.styleable.CustomThumbSizeRecyclerView_thumbLength,
                64F
            ).toInt()
        )

        typedArray.recycle()
    }

    override fun computeHorizontalScrollRange(): Int {
        if (trackLength == -1) {
            trackLength = this.measuredWidth
        }
        return trackLength
    }

    override fun computeHorizontalScrollOffset(): Int {
        getWidths()
        val highestVisiblePixel = super.computeHorizontalScrollOffset()
        return computeScrollOffset(highestVisiblePixel)
    }

    override fun computeHorizontalScrollExtent(): Int {
        return thumbLength
    }

    override fun computeVerticalScrollRange(): Int {
        if (trackLength == -1) {
            trackLength = this.measuredHeight
        }
        return trackLength
    }

    override fun computeVerticalScrollOffset(): Int {
        getHeights()
        val highestVisiblePixel = super.computeVerticalScrollOffset()

        return computeScrollOffset(highestVisiblePixel)
    }

    private fun computeScrollOffset(highestVisiblePixel: Int): Int {
        val invisiblePartOfRecyclerView: Int = totalLength - trackLength
        val scrollAmountRemaining = invisiblePartOfRecyclerView - highestVisiblePixel
        return when {
            invisiblePartOfRecyclerView == scrollAmountRemaining -> {
                0
            }
            scrollAmountRemaining > 0 -> {
                ((trackLength - thumbLength) / (invisiblePartOfRecyclerView.toFloat() / highestVisiblePixel)).roundToInt()
            }
            else -> {
                trackLength - thumbLength
            }
        }
    }

    override fun computeVerticalScrollExtent(): Int {
        return thumbLength
    }

    private fun getHeights() {
        if (totalLength == -1) {
            val rec = this.measuredHeight
            trackLength = rec
            measure(
                MeasureSpec.makeMeasureSpec(this.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            totalLength = this.measuredHeight
            measure(
                MeasureSpec.makeMeasureSpec(this.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(rec, MeasureSpec.AT_MOST)
            )
        }
    }

    private fun getWidths() {
        if (totalLength == -1) {
            val rec = this.measuredWidth
            trackLength = rec
            measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(this.measuredHeight, MeasureSpec.EXACTLY)
            )
            totalLength = this.measuredWidth
            measure(
                MeasureSpec.makeMeasureSpec(rec, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(this.measuredHeight, MeasureSpec.EXACTLY)
            )
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).roundToInt()
    }
}
