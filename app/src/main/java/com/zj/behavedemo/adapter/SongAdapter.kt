package com.zj.behavedemo.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zj.behavedemo.R

class SongAdapter(data: MutableList<Int>?) :
    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_song_layout, data) {

    override fun convert(holder: BaseViewHolder, item: Int) {
        holder.setText(R.id.tv_item_music_position, item.toString())
    }
}