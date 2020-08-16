package com.kinnerapriyap.sugar.mediapreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.ShergilViewModel
import com.kinnerapriyap.sugar.databinding.FragmentMediaPreviewPageBinding
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel

class MediaPreviewPageFragment : Fragment(), View.OnTouchListener {

    private val viewModel: ShergilViewModel by activityViewModels()

    private var binding: FragmentMediaPreviewPageBinding? = null

    private var scaleFactor = 1f

    private lateinit var scaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener

    private val scaleDetector by lazy {
        ScaleGestureDetector(context, scaleListener)
    }

    private val displayModel: MediaCellDisplayModel?
        get() = arguments?.getParcelable(MEDIA_CELL_DISPLAY_MODEL)

    companion object {
        private const val MEDIA_CELL_DISPLAY_MODEL = "media_cell_display_model"

        @JvmStatic
        fun createInstance(displayModel: MediaCellDisplayModel) =
            MediaPreviewPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MEDIA_CELL_DISPLAY_MODEL, displayModel)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_media_preview_page,
            container,
            false
        )
        binding?.root?.setOnTouchListener(this)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.displayModel = displayModel
        binding?.listener = parentFragment as? MediaPreviewFragment
        scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor =
                    1f.coerceAtLeast(
                        scaleFactor.coerceAtMost(
                            viewModel.getChoiceSpec().previewMaxScaleFactor
                        )
                    )
                binding?.imageView?.scaleX = scaleFactor
                binding?.imageView?.scaleY = scaleFactor
                return true
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        scaleDetector.onTouchEvent(event)
        return true
    }
}