package com.kinnerapriyap.sugar

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.databinding.MediaCellListener
import kotlinx.android.synthetic.main.fragment_media_gallery.*

class MediaGalleryFragment : Fragment(), MediaCellListener {

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private val viewModel: ShergilViewModel by viewModels()

    private val mediaGalleryAdapter: MediaGalleryAdapter by lazy {
        MediaGalleryAdapter(this@MediaGalleryFragment)
    }

    companion object {
        fun newInstance() = MediaGalleryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_media_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            layoutManager = GridLayoutManager(requireActivity(), choiceSpec.numOfColumns)
            adapter = mediaGalleryAdapter
        }

        viewModel.getMediaCellDisplayModels().observe(requireActivity(), Observer {
            mediaGalleryAdapter.mediaCellDisplayModels = it
        })

        val images: MutableList<Uri> = mutableListOf()
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
        requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            while (cursor.moveToNext()) {
                /**
                 * Use the ID column from the projection to get
                 * a URI representing the media item
                 */
                val id = cursor.getLong(idColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(contentUri)
            }
        }
        viewModel.initialiseMediaCellDisplayModels(images)
    }

    override fun onMediaCellClicked(uri: Uri) {
        viewModel.setCheckedMedia(uri)
    }
}