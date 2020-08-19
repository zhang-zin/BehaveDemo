package com.zj.behavedemo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.behavedemo.R
import kotlinx.android.synthetic.main.fragment_tab.*

class TabFragment : Fragment() {
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_fragment_tab_text.text = name
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String) =
            TabFragment().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                }
            }
    }
}