package com.zj.behavedemo.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import com.zj.behavedemo.R

class TitleBarBehavior @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    private var contentTransY = 0F
    private var topBarHeight = 0

    init {
        contentTransY = context.resources.getDimension(R.dimen.content_trans_y)
        val statusBarID = context.resources.getIdentifier(
            "status_bar_height",
            "dimen", "android"
        )
        val statusBarHeight = context.resources.getDimension(statusBarID)
        topBarHeight =
            (context.resources.getDimension(R.dimen.top_bar_height) + statusBarHeight).toInt()
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.ll_content
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        //调整TitleBar布局位置紧贴Content顶部
        adjustPosition(parent, child, dependency);
        //这里只计算Content上滑范围一半的百分比
        val start = (contentTransY + topBarHeight) / 2
        val upPro = (contentTransY - MathUtils.clamp(
            dependency.translationY,
            start,
            contentTransY
        )) / (contentTransY - start)
        child.alpha = 1 - upPro
        return true
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        val dependencies = parent.getDependencies(child)
        var dependency: View? = null
        for (view in dependencies) {
            if (view?.id == R.id.ll_content) {
                dependency = view
                break
            }
        }

        return if (dependency != null) {
            // 调整TitleBar的布局位置紧贴再Content的上方
            adjustPosition(parent, child, dependency)
            true
        } else {
            false
        }
    }

    private fun adjustPosition(parent: CoordinatorLayout, child: View, dependency: View) {
        val lp = child.layoutParams as CoordinatorLayout.LayoutParams
        val left = parent.left + lp.leftMargin
        val top = (dependency.y - child.measuredHeight + lp.topMargin).toInt()
        val right = child.measuredWidth + left - parent.paddingRight - lp.rightMargin
        val bottom = (dependency.y - lp.bottomMargin).toInt()
        child.layout(left, top, right, bottom)
    }

}
