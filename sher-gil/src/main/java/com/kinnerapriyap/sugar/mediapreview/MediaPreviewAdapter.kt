package com.kinnerapriyap.sugar.mediapreview

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediapreview.MediaObjectPreviewFragment.Companion.MEDIA_OBJECT_PREVIEW

class MediaPreviewAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var selectedMedia: List<MediaCellDisplayModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = selectedMedia.size

    override fun createFragment(position: Int): Fragment =
        MediaObjectPreviewFragment().apply {
            arguments = Bundle().apply {
                putSerializable(MEDIA_OBJECT_PREVIEW, selectedMedia[position])
            }
        }
}