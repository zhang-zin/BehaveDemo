package com.zj.behavedemo

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.jaeger.library.StatusBarUtil
import com.zj.behavedemo.fragment.SongFragment
import com.zj.behavedemo.fragment.TabFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {

    private val mTitles = arrayOf("热门", "专辑", "视频", "资讯")
    private val mFragments = listOf(
        SongFragment.newInstance(),
        TabFragment.newInstance("专辑页面"),
        TabFragment.newInstance("视频页面"),
        TabFragment.newInstance("资讯页面")
    )
    private val fragmentAdapter = FragmentAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
        initEvent()
    }

    private fun initEvent() {
        vp.adapter = fragmentAdapter
        stl.setViewPager(vp, mTitles)
        //反射修改最少滑动距离

        //反射修改最少滑动距离
        try {
            val mTouchSlop: Field = ViewPager::class.java.getDeclaredField("mTouchSlop")
            mTouchSlop.isAccessible = true
            mTouchSlop.setInt(vp, dp2px(50f))
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        vp.offscreenPageLimit = mFragments.size
    }

    private fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal,
            resources.displayMetrics
        ).toInt()
    }


    inner class FragmentAdapter(
        fm: FragmentManager,
        behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) :
        FragmentPagerAdapter(fm, behavior) {
        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }
    }
}