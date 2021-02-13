package com.kinnerapriyap.sugar.camera

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kinnerapriyap.sugar.ShergilActivity
import com.kinnerapriyap.sugar.ShergilViewModel
import com.kinnerapriyap.sugar.databinding.FragmentCameraBinding

class CameraFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: ShergilViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null

    private var camera: Camera? = null

    private var cameraProvider: ProcessCameraProvider? = null

    private var _binding: FragmentCameraBinding? = null

    private val binding get() = _binding!!

    private var capturedBitmap: Bitmap? = null

    private val cameraFlashSpinnerAdapter by lazy {
        CameraFlashSpinnerAdapter(requireContext())
    }

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF

    companion object {
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val MIME_TYPE = "image/jpeg"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCameraState().observe(
            viewLifecycleOwner,
            { cameraState ->
                binding.captureGroup.isVisible = cameraState == CameraState.CAPTURE
                binding.confirmGroup.isVisible = cameraState == CameraState.CONFIRM
            }
        )

        viewModel.setCameraState(CameraState.CAPTURE)
        binding.viewFinder.post {
            setupCamera()
            setupCameraUI()
        }

        binding.galleryButton.setOnClickListener { onGalleryButtonClicked() }
        binding.cameraCaptureButton.setOnClickListener { onCameraCaptureButtonClicked() }
        binding.switchCameraButton.setOnClickListener { onSwitchCameraButtonClicked() }
        binding.cameraCaptureNoButton.setOnClickListener { onCameraCaptureNoClicked() }
        binding.cameraCaptureYesButton.setOnClickListener { onCameraCaptureYesClicked() }
    }

    private fun setupCameraUI() {
        binding.flashButtonSpinner.onItemSelectedListener = this
        binding.flashButtonSpinner.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                view.performClick()
                cameraFlashSpinnerAdapter.isOpen = true
            }
            false
        }
        binding.flashButtonSpinner.adapter = cameraFlashSpinnerAdapter
    }

    private fun onGalleryButtonClicked() {
        (requireActivity() as? ShergilActivity)?.askPermissionAndOpenGallery()
    }

    private fun onCameraCaptureButtonClicked() = takePhoto()

    private fun onSwitchCameraButtonClicked() {
        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        bindCameraUseCases()
    }

    private fun onCameraCaptureYesClicked() {
        val bitmap = capturedBitmap ?: return
        viewModel.insertCameraImage(
            getFileDisplayName(FILENAME_FORMAT),
            MIME_TYPE,
            bitmap
        )
        (requireActivity() as? ShergilActivity)?.askPermissionAndOpenGallery()
    }

    private fun onCameraCaptureNoClicked() {
        viewModel.setCameraState(CameraState.CAPTURE)
    }

    private fun setupCamera() {
        val activity = activity ?: return
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()

                val hasBackCamera = cameraProvider.hasBackCamera()
                val hasFrontCamera = cameraProvider.hasFrontCamera()
                binding.switchCameraButton.isVisible = hasBackCamera && hasFrontCamera

                lensFacing = when {
                    hasBackCamera -> CameraSelector.LENS_FACING_BACK
                    hasFrontCamera -> CameraSelector.LENS_FACING_FRONT
                    else -> throw IllegalStateException("Back and front camera are unavailable")
                }

                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(activity)
        )
    }

    private fun bindCameraUseCases() {
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialisation failed")

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        imageCapture = ImageCapture.Builder().apply {
            setFlashMode(flashMode)
        }.build()

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(lensFacing).build()

        cameraProvider.unbindAll()
        try {
            camera =
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
        } catch (e: Exception) {
            val msg = "Use case binding failed" + e.message
            activity?.let {
                it.setResult(Activity.RESULT_CANCELED)
                it.finish()
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val activity = activity ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(e: ImageCaptureException) {
                    Toast.makeText(
                        activity,
                        "Photo capture failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    capturedBitmap = image.toBitmap()
                    viewModel.setCameraState(CameraState.CONFIRM)
                    binding.cameraCapturePreviewImage.setImageBitmap(capturedBitmap)
                    super.onCaptureSuccess(image)
                }
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Display flash animation to indicate that photo was captured
            val cameraContainer = binding.cameraContainer
            cameraContainer.postDelayed(
                {
                    cameraContainer.foreground = ColorDrawable(Color.WHITE)
                    cameraContainer.postDelayed(
                        { cameraContainer.foreground = null },
                        ANIMATION_FAST_MILLIS
                    )
                },
                ANIMATION_SLOW_MILLIS
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        setFlashMode((parent?.getItemAtPosition(position) as? CameraFlash)?.flashMode)
    }

    private fun setFlashMode(flashMode: Int?) {
        cameraFlashSpinnerAdapter.isOpen = false
        this.flashMode = flashMode ?: ImageCapture.FLASH_MODE_OFF
        if (cameraProvider != null) bindCameraUseCases()
    }
}
