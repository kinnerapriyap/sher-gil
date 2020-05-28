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
import com.airbnb.epoxy.EpoxyRecyclerView
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.databinding.MediaCellListener

class MediaGalleryFragment : Fragment(), MediaCellListener {

    private val epoxyRecyclerView: EpoxyRecyclerView
        get() = requireView().findViewById(R.id.epoxy_recycler_view)

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private val viewModel: ShergilViewModel by viewModels()

    private val controller: ShergilController by lazy {
        ShergilController(this)
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

        val spanCount = choiceSpec.numOfColumns
        val layoutManager = GridLayoutManager(requireActivity(), spanCount)
        controller.spanCount = spanCount
        layoutManager.spanSizeLookup = controller.spanSizeLookup
        epoxyRecyclerView.layoutManager = layoutManager
        epoxyRecyclerView.setControllerAndBuildModels(controller)

        viewModel.getMediaCellDisplayModels().observe(requireActivity(), Observer {
            controller.mediaCellDisplayModels = it
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