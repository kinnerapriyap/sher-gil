package com.kinnerapriyap.sugar.mediapreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import kotlinx.android.synthetic.main.fragment_media_object_preview.imageView

class MediaObjectPreviewFragment : Fragment() {

    companion object {
        const val MEDIA_OBJECT_PREVIEW = "mediaObjectPreview"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_media_object_preview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(MEDIA_OBJECT_PREVIEW) }?.apply {
            val displayModel =
                getSerializable(MEDIA_OBJECT_PREVIEW) as? MediaCellDisplayModel
            imageView.setImageURI(displayModel?.mediaUri)
        }
    }
}