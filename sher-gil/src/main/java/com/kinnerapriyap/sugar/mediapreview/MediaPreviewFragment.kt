package com.kinnerapriyap.sugar.mediapreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.kinnerapriyap.sugar.ShergilViewModel
import com.kinnerapriyap.sugar.databinding.FragmentMediaPreviewBinding
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel

class MediaPreviewFragment : Fragment() {

    private val viewModel: ShergilViewModel by activityViewModels()

    private val args: MediaPreviewFragmentArgs by navArgs()

    private var _binding: FragmentMediaPreviewBinding? = null

    private val binding get() = _binding!!

    private val mediaPreviewAdapter by lazy {
        MediaPreviewAdapter(::onMediaObjectPreviewClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = mediaPreviewAdapter

        TabLayoutMediator(binding.tabDots, binding.viewPager) { _, _ ->
        }.attach()

        mediaPreviewAdapter.selectedMedia = args.selectedMedia.toList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onMediaObjectPreviewClicked(displayModel: MediaCellDisplayModel) {
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
