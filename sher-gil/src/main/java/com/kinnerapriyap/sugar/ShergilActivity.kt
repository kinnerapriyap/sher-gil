package com.kinnerapriyap.sugar

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.kinnerapriyap.sugar.databinding.ActivityShergilBinding
import com.kinnerapriyap.sugar.mediagallery.album.MediaGalleryAlbumSpinnerAdapter
import com.kinnerapriyap.sugar.mediagallery.album.toBucketDisplayName
import com.kinnerapriyap.sugar.resultlauncher.ResultLauncherHandler

internal class ShergilActivity :
    AppCompatActivity(),
    AdapterView.OnItemSelectedListener {

    private var observer: ResultLauncherHandler? = null

    private val viewModel: ShergilViewModel by viewModels()

    private lateinit var mediaGalleryAlbumSpinnerAdapter: MediaGalleryAlbumSpinnerAdapter

    private lateinit var binding: ActivityShergilBinding

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    companion object {
        const val RESULT_URIS = "resultUris"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        theme.applyStyle(viewModel.getChoiceSpec().theme, true)
        super.onCreate(savedInstanceState)
        binding = ActivityShergilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.previewButton.isVisible = viewModel.getChoiceSpec().allowPreview

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        requestedOrientation = viewModel.getChoiceSpec().orientation

        observer = ResultLauncherHandler(
            this,
            ::setReadStoragePermissionResult,
            ::setWriteStorageAndCameraPermissionsResult,
            ::setCameraCaptureResult
        )

        viewModel.getSelectedMediaCount().observe(
            this,
            { selectedCount ->
                binding.previewButton.isEnabled = selectedCount != 0
                binding.applyButton.isEnabled = selectedCount != 0
                binding.applyButton.text = StringBuilder().apply {
                    append(resources.getString(R.string.apply))
                    if (selectedCount != 0) append(" ($selectedCount)")
                }
            }
        )

        viewModel.getErrorMessage().observe(
            this,
            Observer {
                it ?: return@Observer
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        )

        viewModel.getCursor().observe(
            this,
            Observer {
                it ?: return@Observer
                setToolbarSpinner()
            }
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isMediaGallery = destination.id == R.id.mediaGalleryFragment
            val isMediaPreview = destination.id == R.id.mediaPreviewFragment
            binding.toolbar.isVisible = isMediaGallery
            binding.previewButton.isVisible = isMediaGallery
            binding.bottombar.isVisible = isMediaGallery || isMediaPreview
        }

        binding.previewButton.setOnClickListener { onPreviewClicked() }
        binding.applyButton.setOnClickListener { onApplyClicked() }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.getChoiceSpec().showCameraFirst) {
            askPermissionAndOpenCameraCapture()
        } else {
            askPermissionAndOpenGallery()
        }
    }

    fun askPermissionAndOpenGallery() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ->
                observer?.askReadStoragePermission()
            else -> {
                openMediaGallery()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun setShergilResult() {
        val resultIntent =
            Intent().apply {
                putParcelableArrayListExtra(
                    RESULT_URIS,
                    viewModel.getSelectedMediaUriList() as? ArrayList
                )
            }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun setReadStoragePermissionResult(allowed: Boolean) {
        if (allowed) {
            openMediaGallery()
        } else {
            setResultCancelledAndFinish()
        }
    }

    private fun setWriteStorageAndCameraPermissionsResult(map: Map<String, Boolean>) {
        if (map[CAMERA] == true &&
            (Build.VERSION.SDK_INT > Build.VERSION_CODES.P || map[WRITE_EXTERNAL_STORAGE] == true)
        ) {
            openCameraCapture()
        } else {
            setResultCancelledAndFinish()
        }
    }

    private fun setToolbarSpinner() {
        mediaGalleryAlbumSpinnerAdapter =
            MediaGalleryAlbumSpinnerAdapter(this, viewModel.fetchAlbums())
        binding.albumSpinner.adapter = mediaGalleryAlbumSpinnerAdapter
        binding.albumSpinner.onItemSelectedListener = this
        binding.albumSpinner.setSelection(0)
    }

    fun askPermissionAndOpenCameraCapture() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ->
                observer?.askWriteStorageAndCameraPermission()
            else -> {
                openCameraCapture()
            }
        }
    }

    private fun openCameraCapture() {
        if (viewModel.getChoiceSpec().showDeviceCamera) {
            viewModel.resetCameraCaptureUri()
            observer?.cameraCapture(viewModel.getCameraCaptureUri())
        } else {
            Handler(Looper.getMainLooper()).post {
                navController.navigate(NavGraphDirections.actionGlobalCameraFragment())
            }
        }
    }

    private fun setCameraCaptureResult(result: Boolean) = Unit

    private fun openMediaGallery() {
        navController.navigate(NavGraphDirections.actionGlobalMediaGalleryFragment())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        viewModel.setSelectedAlbumSpinnerName(null)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setSelectedAlbumSpinnerName(
            mediaGalleryAlbumSpinnerAdapter.getItem(position)?.toBucketDisplayName()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        observer = null
    }

    private fun onApplyClicked() {
        setShergilResult()
    }

    private fun onPreviewClicked() {
        val action =
            NavGraphDirections.actionGlobalMediaPreviewFragment(
                viewModel.getSelectedMediaCellDisplayModels().toTypedArray()
            )
        navController.navigate(action)
    }

    private fun setResultCancelledAndFinish() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
