package com.zj.behavedemo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zj.behavedemo.R
import com.zj.behavedemo.adapter.SongAdapter
import kotlinx.android.synthetic.main.fragment_song.*
import java.util.*

class SongFragment : Fragment() {

    private val songLists = ArrayList<Int>()
    private var adapter: SongAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            SongFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_song_list.adapter = adapter
    }

    private fun initData() {
        for (i in 0..31) {
            songLists.add(i)
        }
        adapter = SongAdapter(songLists)
    }

}