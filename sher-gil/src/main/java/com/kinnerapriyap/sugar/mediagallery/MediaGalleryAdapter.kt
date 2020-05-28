package com.kinnerapriyap.sugar.mediagallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.databinding.MediaCellDisplayModel
import com.kinnerapriyap.sugar.databinding.MediaCellListener
import com.kinnerapriyap.sugar.databinding.ViewMediaCellBinding

class MediaGalleryAdapter(
    private val mediaCellListener: MediaCellListener
) : RecyclerView.Adapter<MediaGalleryAdapter.MediaCellHolder>() {

    var mediaCellDisplayModels: List<MediaCellDisplayModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaCellHolder {
        val binding = DataBindingUtil.inflate<ViewMediaCellBinding>(
            LayoutInflater.from(parent.context),
            R.layout.view_media_cell,
            parent,
            false
        )
        return MediaCellHolder(binding)
    }

    override fun getItemCount(): Int = mediaCellDisplayModels.size

    override fun onBindViewHolder(holder: MediaCellHolder, position: Int) =
        holder.bind(mediaCellDisplayModels[position], mediaCellListener)

    inner class MediaCellHolder(
        private val binding: ViewMediaCellBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(displayModel: MediaCellDisplayModel, listener: MediaCellListener) {
            binding.displayModel = displayModel
            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}