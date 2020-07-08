package com.kinnerapriyap.sugar.camera

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.ShergilActivity
import com.kinnerapriyap.sugar.databinding.FragmentCameraBinding
import java.io.File

class CameraFragment : Fragment(), CameraUIListener, AdapterView.OnItemSelectedListener {

    private var imageCapture: ImageCapture? = null

    private var camera: Camera? = null

    private var cameraProvider: ProcessCameraProvider? = null

    private lateinit var outputDirectory: File

    private var binding: FragmentCameraBinding? = null

    private val cameraFlashSpinnerAdapter by lazy {
        CameraFlashSpinnerAdapter(requireContext())
    }

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outputDirectory = getOutputDirectory(activity ?: return)

        binding?.isCapture = true
        binding?.viewFinder?.post {
            setupCamera()
            setupCameraUI()
        }
    }

    private fun setupCameraUI() {
        binding?.listener = this
        binding?.flashButtonSpinner?.onItemSelectedListener = this
        binding?.flashButtonSpinner?.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                view.performClick()
                cameraFlashSpinnerAdapter.isOpen = true
            }
            false
        }
        binding?.flashButtonSpinner?.adapter = cameraFlashSpinnerAdapter
    }

    override fun onGalleryButtonClicked() {
        (requireActivity() as? ShergilActivity)?.askPermissionAndOpenGallery()
    }

    override fun onCameraCaptureButtonClicked() = takePhoto()

    override fun onSwitchCameraButtonClicked() {
        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        bindCameraUseCases()
    }

    override fun onCameraCaptureYesClicked() {
        (requireActivity() as? ShergilActivity)?.askPermissionAndOpenGallery()
    }

    override fun onCameraCaptureNoClicked() {
        binding?.isCapture = true
    }

    private fun setupCamera() {
        val activity = activity ?: return
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener(
            Runnable {
                cameraProvider = cameraProviderFuture.get()

                val hasBackCamera = cameraProvider.hasBackCamera()
                val hasFrontCamera = cameraProvider.hasFrontCamera()
                binding?.switchCameraButton?.isVisible = hasBackCamera && hasFrontCamera

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
        preview.setSurfaceProvider(binding?.viewFinder?.createSurfaceProvider())

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
        val photoFile = createFile(outputDirectory, FILENAME_FORMAT, PHOTO_EXTENSION)
        val metadata = Metadata().apply {
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }
        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Toast.makeText(
                        activity,
                        "Photo capture failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(savedUri.toFile().extension)
                    MediaScannerConnection.scanFile(
                        activity,
                        arrayOf(savedUri.toFile().absolutePath),
                        arrayOf(mimeType)
                    ) { _, uri ->
                        binding?.isCapture = false
                        binding?.cameraCapturePreviewImage?.setImageURI(uri)
                    }
                }
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Display flash animation to indicate that photo was captured
            val cameraContainer = binding?.cameraContainer ?: return
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
        binding = null
        super.onDestroyView()
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
