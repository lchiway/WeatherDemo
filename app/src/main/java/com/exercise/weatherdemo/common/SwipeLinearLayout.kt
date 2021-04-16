package com.exercise.weatherdemo.common

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.Scroller
import kotlin.math.abs

/**
 * @author lvzw
 * @date 2021年04月15日 16:46
 */

class SwipeLinearLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {
    var mScroller: Scroller
    var startScrollX = 0
    var lastX = 0f
    var lastY = 0f
    var startX = 0f
    var startY = 0f
    var hasJudged = false
    var ignore = false
    var onSwipeListener: OnSwipeListener? = null

    // 左边部分, 即从开始就显示的部分的长度
    private var width_left = 0

    // 右边部分, 即在开始时是隐藏的部分的长度
    private var width_right = 0
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val left = getChildAt(0)
        val right = getChildAt(1)
        width_left = left.measuredWidth
        width_right = right.measuredWidth
    }

    private fun disallowParentsInterceptTouchEvent(parent: ViewParent?) {
        if (null == parent) {
            return
        }
        parent.requestDisallowInterceptTouchEvent(true)
        disallowParentsInterceptTouchEvent(parent.parent)
    }

    private fun allowParentsInterceptTouchEvent(parent: ViewParent?) {
        if (null == parent) {
            return
        }
        parent.requestDisallowInterceptTouchEvent(false)
        allowParentsInterceptTouchEvent(parent.parent)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                disallowParentsInterceptTouchEvent(parent)
                hasJudged = false
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = ev.x
                val curY = ev.y
                if (!hasJudged) {
                    val dx = curX - startX
                    val dy = curY - startY
                    if (dx * dx + dy * dy > MOVE_JUDGE_DISTANCE * MOVE_JUDGE_DISTANCE) {
                        if (abs(dy) > abs(dx)) {
                            allowParentsInterceptTouchEvent(parent)
                            if (null != onSwipeListener) {
                                onSwipeListener!!.onDirectionJudged(this, false)
                            }
                        } else {
                            if (null != onSwipeListener) {
                                onSwipeListener!!.onDirectionJudged(this, true)
                            }
                            lastX = curX
                            lastY = curY
                        }
                        hasJudged = true
                        ignore = true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (hasJudged) {
            true
        } else super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                //Log.d("lvzw", "onTouchEvent: touch down: $event")
                lastX = event.x
                lastY = event.y
                startScrollX = scrollX
            }
            MotionEvent.ACTION_MOVE -> {
                //Log.d("lvzw", "onTouchEvent: touch move: $event")
                if (ignore) {
                    ignore = false
                }
                val curX = event.x
                val dX = curX - lastX
                lastX = curX
                if (hasJudged) {
                    val targetScrollX = scrollX + (-dX).toInt()
                    when {
                        targetScrollX > width_right -> scrollTo(width_right, 0)
                        targetScrollX < 0 -> scrollTo(0, 0)
                        else -> scrollTo(targetScrollX, 0)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                //Log.d("lvzw", "onTouchEvent: touch up: $event")
                val finalX = event.x
                if (finalX < startX) {
                    scrollAuto(DIRECTION_EXPAND)
                } else {
                    scrollAuto(DIRECTION_SHRINK)
                }
            }
        }
        return true
    }

    /**
     * 自动滚动， 变为展开或收缩状态
     * @param direction
     */
    fun scrollAuto(direction: Int) {
        val curScrollX = scrollX
        if (direction == DIRECTION_EXPAND) {
            // 展开
            mScroller.startScroll(curScrollX, 0, width_right - curScrollX, 0, 300)
        } else {
            // 缩回
            mScroller.startScroll(curScrollX, 0, -curScrollX, 0, 300)
        }
        invalidate()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, 0)
            invalidate()
        }
    }

    fun setSwipeListener(listener: OnSwipeListener?) {
        onSwipeListener = listener
    }

    interface OnSwipeListener {
        /**
         * 手指滑动方向明确了
         * @param sll  拖动的SwipeLinearLayout
         * @param isHorizontal 滑动方向是否为水平
         */
        fun onDirectionJudged(sll: SwipeLinearLayout?, isHorizontal: Boolean)
    }

    companion object {
        const val DIRECTION_EXPAND = 0
        const val DIRECTION_SHRINK = 1
        var MOVE_JUDGE_DISTANCE = 5f
    }

    init {
        mScroller = Scroller(context)
        this.orientation = HORIZONTAL
    }
}