package com.kinnerapriyap.sugar.resultlauncher

import android.Manifest
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
    private val setCameraPermissionResult: (Boolean) -> Unit
) : LifecycleObserver, ResultLauncher {

    private val askReadStoragePermission: ActivityResultLauncher<String> =
        registry.register(
            REQUEST_READ_STORAGE_PERMISSION,
            ActivityResultContracts.RequestPermission()
        ) { allowed ->
            setReadStoragePermissionResult(allowed)
        }

    private val askCameraPermission: ActivityResultLauncher<String> =
        registry.register(
            REQUEST_CAMERA_PERMISSION,
            ActivityResultContracts.RequestPermission()
        ) { allowed ->
            setCameraPermissionResult(allowed)
        }

    companion object {
        private const val REQUEST_READ_STORAGE_PERMISSION = "request_read_storage_permission"
        private const val REQUEST_CAMERA_PERMISSION = "request_camera_permission"
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        askReadStoragePermission.unregister()
        askCameraPermission.unregister()
    }

    override fun askReadStoragePermission() {
        askReadStoragePermission.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun askCameraPermission() {
        askCameraPermission.launch(
            Manifest.permission.CAMERA
        )
    }
}
