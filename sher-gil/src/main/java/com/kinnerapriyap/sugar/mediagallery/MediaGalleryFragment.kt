package com.kinnerapriyap.sugar.mediagallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FilterQueryProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.ShergilViewModel
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.CAMERA_CAPTURE_ID
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellListener
import com.kinnerapriyap.sugar.mediagallery.media.MediaGalleryAdapter
import kotlinx.android.synthetic.main.fragment_media_gallery.view.*

class MediaGalleryFragment : Fragment(), MediaCellListener {

    private val viewModel: ShergilViewModel by activityViewModels()

    private lateinit var mediaGalleryAdapter: MediaGalleryAdapter

    private var listener: MediaGalleryFragmentListener? = null

    companion object {
        fun newInstance() =
            MediaGalleryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_media_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return

        view.recyclerView.layoutManager =
            GridLayoutManager(activity, viewModel.getChoiceSpec().numOfColumns)

        viewModel.getCursor().observe(
            activity,
            Observer {
                it ?: return@Observer
                mediaGalleryAdapter = MediaGalleryAdapter(
                    viewModel.getCurrentMediaCursor(),
                    viewModel.getSelectedMediaCellDisplayModels(),
                    this@MediaGalleryFragment,
                    viewModel.getChoiceSpec().mimeTypes,
                    viewModel.allowMultipleSelection()
                )
                view.recyclerView.adapter = mediaGalleryAdapter
                mediaGalleryAdapter.filterQueryProvider = FilterQueryProvider { filter ->
                    viewModel.getCurrentMediaCursor(filter.toString())
                }
                listener?.setToolbarSpinner()
            }
        )

        viewModel.getMediaCellUpdateModel().observe(
            activity,
            Observer { updateModel ->
                if (updateModel.positions.first == -1) return@Observer
                mediaGalleryAdapter.mediaCellUpdateModel = updateModel
            }
        )

        viewModel.fetchCursor()
    }

    override fun onDestroyView() {
        view?.recyclerView?.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onMediaCellClicked(displayModel: MediaCellDisplayModel) {
        if (displayModel.id == CAMERA_CAPTURE_ID)
            listener?.askPermissionAndOpenCameraCapture()
        else
            viewModel.setMediaChecked(displayModel)
    }

    fun setSelectedSpinnerName(bucketDisplayName: String?) {
        mediaGalleryAdapter.filter.filter(bucketDisplayName)
    }

    fun setMediaGalleryFragmentListener(listener: MediaGalleryFragmentListener) {
        this.listener = listener
    }
}
