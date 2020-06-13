package com.kinnerapriyap.sugar.mediapreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.databinding.ViewMediaObjectPreviewBinding
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel

class MediaPreviewAdapter : RecyclerView.Adapter<MediaPreviewAdapter.MediaPreviewObjectHolder>() {

    var selectedMedia: List<MediaCellDisplayModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = selectedMedia.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaPreviewObjectHolder {
        val binding = DataBindingUtil.inflate<ViewMediaObjectPreviewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.view_media_object_preview,
            parent,
            false
        )
        return MediaPreviewObjectHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaPreviewObjectHolder, position: Int) {
        holder.bind(selectedMedia[position])
    }

    inner class MediaPreviewObjectHolder(
        private val binding: ViewMediaObjectPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(displayModel: MediaCellDisplayModel) {
            binding.displayModel = displayModel
            binding.executePendingBindings()
        }
    }
}