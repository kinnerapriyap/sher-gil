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
    private val setPermissionResult: (Boolean) -> Unit,
    private val setCameraCaptureResult: (Boolean) -> Unit
) : LifecycleObserver, ResultLauncher {

    private val askReadStoragePermission: ActivityResultLauncher<String> =
        registry.register(
            REQUEST_READ_STORAGE_PERMISSION,
            ActivityResultContracts.RequestPermission()
        ) { allowed ->
            setPermissionResult(allowed)
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
        private const val REQUEST_CAMERA_CAPTURE = "request_camera_capture"
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        askReadStoragePermission.unregister()
        cameraCapture.unregister()
    }

    override fun askReadStoragePermission() {
        askReadStoragePermission.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun cameraCapture(uri: Uri?) {
        cameraCapture.launch(uri)
    }
}
