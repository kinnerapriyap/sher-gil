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
import com.kinnerapriyap.sugar.ShergilActivity
import com.kinnerapriyap.sugar.ShergilViewModel
import com.kinnerapriyap.sugar.databinding.FragmentMediaGalleryBinding
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.CAMERA_CAPTURE_ID
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.media.MediaGalleryAdapter

class MediaGalleryFragment : Fragment() {

    private val viewModel: ShergilViewModel by activityViewModels()

    private lateinit var mediaGalleryAdapter: MediaGalleryAdapter

    private var _binding: FragmentMediaGalleryBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return

        binding.recyclerView.layoutManager =
            GridLayoutManager(activity, viewModel.getChoiceSpec().numOfColumns)

        viewModel.getCursor().observe(
            viewLifecycleOwner,
            Observer {
                it ?: return@Observer
                mediaGalleryAdapter = MediaGalleryAdapter(
                    viewModel.getCurrentMediaCursor(),
                    viewModel.getSelectedMediaCellDisplayModels(),
                    ::onMediaCellClicked,
                    viewModel.getChoiceSpec().mimeTypes,
                    viewModel.allowMultipleSelection()
                )
                binding.recyclerView.adapter = mediaGalleryAdapter
                mediaGalleryAdapter.filterQueryProvider = FilterQueryProvider { filter ->
                    viewModel.getCurrentMediaCursor(filter.toString())
                }
                mediaGalleryAdapter.filter.filter("")
            }
        )

        viewModel.getMediaCellUpdateModel().observe(
            viewLifecycleOwner,
            Observer { updateModel ->
                if (updateModel.positions.first == -1) return@Observer
                mediaGalleryAdapter.mediaCellUpdateModel = updateModel
            }
        )

        viewModel.getSelectedAlbumSpinnerName().observe(
            viewLifecycleOwner,
            { bucketDisplayName ->
                if (this::mediaGalleryAdapter.isInitialized) {
                    mediaGalleryAdapter.filter.filter(bucketDisplayName)
                }
            }
        )
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.fetchCursor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }

    private fun onMediaCellClicked(displayModel: MediaCellDisplayModel) {
        if (displayModel.id == CAMERA_CAPTURE_ID)
            (requireActivity() as? ShergilActivity)?.askPermissionAndOpenCameraCapture()
        else
            viewModel.setMediaChecked(displayModel)
    }
}
