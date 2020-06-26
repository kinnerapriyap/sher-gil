package com.kinnerapriyap.sugar.resultlauncher

import android.Manifest
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

internal class ResultLauncherImpl(
    registry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    private val setReadStoragePermissionResult: (Boolean) -> Unit,
    private val setWriteStorageAndCameraPermissionResult: (Map<String, Boolean>) -> Unit,
    private val setCameraCaptureResult: (Boolean) -> Unit
) : LifecycleObserver, ResultLauncher {

    private val askReadStoragePermission: ActivityResultLauncher<String> =
        registry.register(
            REQUEST_READ_STORAGE_PERMISSION,
            ActivityResultContracts.RequestPermission()
        ) { allowed ->
            setReadStoragePermissionResult(allowed)
        }

    private val askWriteStorageAndCameraPermission: ActivityResultLauncher<Array<String>> =
        registry.register(
            REQUEST_WRITE_STORAGE_AND_CAMERA_PERMISSION,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            setWriteStorageAndCameraPermissionResult(map)
        }

    private val cameraCapture: ActivityResultLauncher<Uri> =
        registry.register(
            REQUEST_CAMERA_CAPTURE,
            ActivityResultContracts.TakePicture()
        ) { result ->
            setCameraCaptureResult(result)
        }

    companion object {
        private const val REQUEST_READ_STORAGE_PERMISSION = "request_read_storage_permission"
        private const val REQUEST_WRITE_STORAGE_AND_CAMERA_PERMISSION =
            "request_write_storage_and_camera_permission"
        private const val REQUEST_CAMERA_CAPTURE = "request_camera_capture"
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        askReadStoragePermission.unregister()
        askWriteStorageAndCameraPermission.unregister()
        cameraCapture.unregister()
    }

    override fun askReadStoragePermission() {
        askReadStoragePermission.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun askWriteStorageAndCameraPermission() {
        askWriteStorageAndCameraPermission.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    override fun cameraCapture(uri: Uri?) {
        cameraCapture.launch(uri)
    }
}
