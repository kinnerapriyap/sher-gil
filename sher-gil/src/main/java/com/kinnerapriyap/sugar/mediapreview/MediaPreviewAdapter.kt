package com.kinnerapriyap.sugar.mediapreview

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel

class MediaPreviewAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var selectedMedia: List<MediaCellDisplayModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = selectedMedia.size

    override fun createFragment(position: Int): Fragment =
        MediaPreviewPageFragment.createInstance(selectedMedia[position])
}
