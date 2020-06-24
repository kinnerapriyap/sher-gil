package com.kinnerapriyap.sugar

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.kinnerapriyap.sugar.camera.CameraFragment
import com.kinnerapriyap.sugar.camera.CameraFragmentListener
import com.kinnerapriyap.sugar.databinding.ActivityShergilBinding
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryFragment
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryFragmentListener
import com.kinnerapriyap.sugar.mediagallery.album.MediaGalleryAlbumCursorAdapter
import com.kinnerapriyap.sugar.mediapreview.MediaPreviewFragment
import com.kinnerapriyap.sugar.mediapreview.MediaPreviewFragmentListener
import com.kinnerapriyap.sugar.resultlauncher.ResultLauncherHandler
import java.util.ArrayList

internal class ShergilActivity :
    AppCompatActivity(),
    AdapterView.OnItemSelectedListener,
    MediaGalleryFragmentListener,
    MediaPreviewFragmentListener,
    CameraFragmentListener,
    ShergilActivityListener {

    private lateinit var observer: ResultLauncherHandler

    private val viewModel: ShergilViewModel by viewModels()

    private lateinit var mediaGalleryAlbumCursorAdapter: MediaGalleryAlbumCursorAdapter

    private lateinit var binding: ActivityShergilBinding

    companion object {
        const val RESULT_URIS = "resultUris"
        private const val MEDIA_GALLERY_FRAGMENT_TAG = "mediaGalleryFragmentTag"
        private const val MEDIA_PREVIEW_FRAGMENT_TAG = "mediaPreviewFragmentTag"
    }

    override fun onAttachFragment(fragment: Fragment) {
        when (fragment) {
            is MediaGalleryFragment ->
                fragment.setMediaGalleryFragmentListener(this)
            is MediaPreviewFragment ->
                fragment.setMediaPreviewFragmentListener(this)
            is CameraFragment ->
                fragment.setCameraFragmentListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        theme.applyStyle(viewModel.getChoiceSpec().theme, true)
        super.onCreate(savedInstanceState)
        binding = ActivityShergilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.listener = this
        binding.lifecycleOwner = this
        binding.previewButton.isVisible = viewModel.getChoiceSpec().allowPreview

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        observer = ResultLauncherHandler(
            this,
            ::setReadStoragePermissionResult,
            ::setCameraPermissionResult
        )

        viewModel.getSelectedMediaCount().observe(
            this,
            Observer(binding::setSelectedCount)
        )

        viewModel.getErrorMessage().observe(
            this,
            Observer {
                it ?: return@Observer
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        )

        askPermissionAndOpenGallery()
    }

    private fun askPermissionAndOpenGallery() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ->
                observer.askReadStoragePermission()
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
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun setCameraPermissionResult(allowed: Boolean) {
        if (allowed) {
            openCameraCapture()
        }
    }

    private fun setCameraCaptureResult(result: Boolean) {
        if (result) viewModel.fetchCursor()
    }

    override fun setToolbarSpinner() {
        mediaGalleryAlbumCursorAdapter =
            MediaGalleryAlbumCursorAdapter(this, viewModel.fetchAlbumCursor())
                .also { adapter ->
                    adapter.setDropDownViewResource(R.layout.album_spinner_item)
                }
        binding.albumSpinner.adapter = mediaGalleryAlbumCursorAdapter
        binding.albumSpinner.onItemSelectedListener = this
    }

    override fun askPermissionAndOpenCameraCapture() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED ->
                observer.askCameraPermission()
            else -> {
                openCameraCapture()
            }
        }
    }

    private fun openCameraCapture() {
        viewModel.resetCameraCaptureUri()
        observer.cameraCapture(viewModel.getCameraCaptureUri())
    }

    override fun openMediaGallery() {
        supportFragmentManager.commit {
            replace(
                R.id.container,
                MediaGalleryFragment.newInstance(),
                MEDIA_GALLERY_FRAGMENT_TAG
            )
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        setSelectedSpinnerName(null)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cursor = mediaGalleryAlbumCursorAdapter.getItem(position) as? Cursor
        val bucketDisplayName =
            mediaGalleryAlbumCursorAdapter.convertToString(cursor).toString()
        setSelectedSpinnerName(bucketDisplayName)
    }

    private fun setSelectedSpinnerName(bucketDisplayName: String?) {
        val mediaGalleryFragment: MediaGalleryFragment? =
            supportFragmentManager.findFragmentByTag(MEDIA_GALLERY_FRAGMENT_TAG) as? MediaGalleryFragment
        mediaGalleryFragment?.setSelectedSpinnerName(bucketDisplayName)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeCursor()
    }

    override fun onApplyClicked() {
        setShergilResult()
    }

    override fun onPreviewClicked() {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.container,
                MediaPreviewFragment.newInstance(viewModel.getSelectedMediaCellDisplayModels()),
                MEDIA_PREVIEW_FRAGMENT_TAG
            )
        }
    }

    override fun hideSpinnerAndPreviewButton() {
        binding.toolbar.isVisible = false
        binding.previewButton.isVisible = false
    }

    override fun showSpinnerAndPreviewButton() {
        binding.toolbar.isVisible = true
        binding.previewButton.isVisible = true
    }

    override fun hideBars() {
        binding.toolbar.isVisible = false
        binding.bottombar.isVisible = false
    }

    override fun showBars() {
        binding.toolbar.isVisible = true
        binding.bottombar.isVisible = true
    }
}
