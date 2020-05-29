package com.kinnerapriyap.sugar.mediagallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.ShergilViewModel
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.databinding.MediaCellListener
import kotlinx.android.synthetic.main.fragment_media_gallery.*

class MediaGalleryFragment : Fragment(), MediaCellListener {

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private val viewModel: ShergilViewModel by activityViewModels()

    private val mediaGalleryAdapter: MediaGalleryAdapter by lazy {
        MediaGalleryAdapter(
            viewModel.fetchMediaCursor(),
            this@MediaGalleryFragment
        )
    }

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

        recyclerView.apply {
            layoutManager = GridLayoutManager(requireActivity(), choiceSpec.numOfColumns)
            adapter = mediaGalleryAdapter
        }

        viewModel.getUpdatedMediaCellPosition().observe(requireActivity(), Observer {
            mediaGalleryAdapter.updatedMediaCellPosition = it
        })
    }

    override fun onMediaCellClicked(displayModel: MediaCellDisplayModel) {
        viewModel.setMediaChecked(displayModel)
    }
}