package com.kinnerapriyap.sugar.mediapreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.ShergilViewModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import kotlinx.android.synthetic.main.fragment_media_preview.*
import java.io.Serializable

class MediaPreviewFragment : Fragment(), MediaObjectPreviewListener {

    private val viewModel: ShergilViewModel by activityViewModels()

    private val args: MediaPreviewFragmentArgs by navArgs()

    private val mediaPreviewAdapter by lazy {
        MediaPreviewAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_media_preview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.offscreenPageLimit = 1
        viewPager.adapter = mediaPreviewAdapter

        TabLayoutMediator(tabDots, viewPager) { tab, position ->
        }.attach()

        mediaPreviewAdapter.selectedMedia = args.selectedMedia.toList()
    }

    override fun onMediaObjectPreviewClicked(displayModel: MediaCellDisplayModel) {
        val selectedMedia = mediaPreviewAdapter.selectedMedia
        mediaPreviewAdapter.selectedMedia =
            selectedMedia.map {
                it.copy(
                    isChecked = if (it.id == displayModel.id) !it.isChecked else it.isChecked
                )
            }
        viewModel.setMediaChecked(displayModel)
    }
}
