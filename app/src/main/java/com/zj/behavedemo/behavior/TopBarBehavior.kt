package com.zj.behavedemo.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import com.zj.behavedemo.R

class TopBarBehavior @JvmOverloads constructor(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    private var contentTransY = 0F
    private var topBarHeight = 0f

    init {
        contentTransY = context.resources.getDimension(R.dimen.content_trans_y)
        val statusBarID = context.resources.getIdentifier(
            "status_bar_height",
            "dimen", "android"
        )
        val statusBarHeight = context.resources.getDimension(statusBarID)
        topBarHeight = context.resources.getDimension(R.dimen.top_bar_height) + statusBarHeight
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
        //计算Content上滑的百分比，设置子view的透明度
        val upPro: Float = (contentTransY - MathUtils.clamp(
            dependency.translationY,
            topBarHeight,
            contentTransY
        )) / (contentTransY - topBarHeight)
        val tvName = child.findViewById<View>(R.id.tv_top_bar_name)
        val tvColl = child.findViewById<View>(R.id.tv_top_bar_coll)
        tvName.alpha = upPro
        tvColl.alpha = upPro
        return true
    }
}