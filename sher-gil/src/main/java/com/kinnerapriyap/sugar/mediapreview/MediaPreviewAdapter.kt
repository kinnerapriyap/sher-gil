package com.kinnerapriyap.sugar.mediapreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kinnerapriyap.sugar.databinding.ViewMediaObjectPreviewBinding
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.bindMediaUri

class MediaPreviewAdapter(
    private val onMediaObjectPreviewClicked: ((MediaCellDisplayModel) -> Unit)
) : RecyclerView.Adapter<MediaPreviewAdapter.MediaPreviewObjectHolder>() {

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
        val binding = ViewMediaObjectPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaPreviewObjectHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaPreviewObjectHolder, position: Int) {
        holder.bind(selectedMedia[position], onMediaObjectPreviewClicked)
    }

    inner class MediaPreviewObjectHolder(
        private val binding: ViewMediaObjectPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            displayModel: MediaCellDisplayModel,
            onMediaObjectPreviewClicked: ((MediaCellDisplayModel) -> Unit)
        ) {
            binding.imageView.bindMediaUri(displayModel.mediaUri)
            binding.previewCheckBox.isChecked = displayModel.isChecked
            binding.previewCheckBox.setOnClickListener {
                onMediaObjectPreviewClicked.invoke(displayModel)
            }
        }
    }
}
