package com.zj.behavedemo.behavior

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.zj.behavedemo.R

class ContentBehavior @JvmOverloads constructor(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    private var topBarHeight = 0
    private var contentTransY = 0F // 滑动内容初始化TransY
    private var downEndY = 0f // 下滑的终点
    private var restoreAnimator: ValueAnimator = ValueAnimator() // 收起内容时的动画
    private var mLlContent: View? = null
    private var flingFromCollaps = false //fling是否从折叠状态发生的


    init {
        contentTransY = context.resources.getDimension(R.dimen.content_trans_y)
        val statusBarID = context.resources.getIdentifier(
            "status_bar_height",
            "dimen", "android"
        )
        val statusBarHeight = context.resources.getDimension(statusBarID)
        topBarHeight =
            (context.resources.getDimension(R.dimen.top_bar_height) + statusBarHeight).toInt()
        downEndY = context.resources.getDimension(R.dimen.content_trans_down_end_y)
        restoreAnimator.addUpdateListener {

        }
    }

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        val childLpHeight = child.layoutParams.height
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT
            || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT
        ) {
            // 先获取CoordinatorLayout的测量规格信息，若不指定具体高度则使用CoordinatorLayout的高度
            var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
            if (availableHeight == 0) {
                availableHeight = parent.height
            }
            // 设置Content的高度
            val height = availableHeight - topBarHeight
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                height,
                if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT) View.MeasureSpec.EXACTLY else View.MeasureSpec.AT_MOST
            )
            parent.onMeasureChild(
                child,
                parentWidthMeasureSpec,
                widthUsed,
                heightMeasureSpec,
                heightUsed
            )
            return true
        }
        return false
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        mLlContent = child
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        flingFromCollaps = child.translationX <= contentTransY
        return false
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return directTargetChild.id == R.id.ll_content && axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScrollAccepted(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ) {
        if (restoreAnimator.isStarted) {
            restoreAnimator.cancel()
        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        type: Int
    ) {
        //如果是从初始状态转换到展开状态过程触发收起动画
        if (child.translationY > contentTransY) {
            restore()
        }
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val transY = child.translationY - dy

        if (dy > 0) {
            // 处理上滑
            if (transY >= topBarHeight) {
                translationByConsume(child, transY, consumed, dy)
            } else {
                translationByConsume(
                    child, topBarHeight.toFloat(), consumed,
                    (child.translationY - topBarHeight).toInt()
                )
            }
        }

        if (dy < 0 && !target.canScrollVertically(-1)) {
            //下滑时处理Fling,折叠时下滑Recycler(或NestedScrollView) Fling滚动到contentTransY停止Fling
            if (type == ViewCompat.TYPE_NON_TOUCH && transY >= contentTransY && flingFromCollaps) {
                flingFromCollaps = false
                translationByConsume(child, contentTransY, consumed, dy.toFloat().toInt())
                stopViewScroll(target)
                return
            }

            //处理下滑
            if (transY >= topBarHeight && transY <= downEndY) {
                translationByConsume(child, transY, consumed, dy.toFloat().toInt())
            } else {
                translationByConsume(
                    child,
                    downEndY,
                    consumed,
                    (downEndY - child.translationY).toInt()
                )
                stopViewScroll(target)
            }
        }
    }

    override fun onDetachedFromLayoutParams() {
        if (restoreAnimator.isStarted) {
            restoreAnimator.cancel()
            restoreAnimator.removeAllUpdateListeners()
            restoreAnimator.removeAllListeners()
        }
        super.onDetachedFromLayoutParams()
    }


    private fun translationByConsume(
        child: View,
        translationY: Float,
        consumed: IntArray,
        consumedDy: Int
    ) {
        child.translationY = translationY
        consumed[1] = consumedDy
    }

    private fun stopViewScroll(target: View) {
        if (target is RecyclerView) {
            target.stopScroll()
        }
        if (target is NestedScrollView) {
            try {
                val clazz: Class<out NestedScrollView?> = target.javaClass
                val mScroller = clazz.getDeclaredField("mScroller")
                mScroller.isAccessible = true
                val overScroller = mScroller[target] as OverScroller
                overScroller.abortAnimation()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun restore() {
        if (restoreAnimator.isStarted) {
            restoreAnimator.cancel()
            restoreAnimator.removeAllListeners()
        }
        restoreAnimator.setFloatValues(mLlContent?.translationY ?: 0f, contentTransY)
        restoreAnimator.duration = 200L
        restoreAnimator.start()
    }
}