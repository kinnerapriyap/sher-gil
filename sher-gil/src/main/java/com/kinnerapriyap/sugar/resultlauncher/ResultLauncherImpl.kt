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
    private val setPermissionResult: (Boolean) -> Unit
) : LifecycleObserver, ResultLauncher {

    private val askReadStoragePermission: ActivityResultLauncher<String> =
        registry.register(
            REQUEST_PERMISSION,
            ActivityResultContracts.RequestPermission()
        ) { allowed ->
            setPermissionResult(allowed)
        }

    companion object {
        private const val REQUEST_PERMISSION = "request_permission"
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        askReadStoragePermission.unregister()
    }

    override fun askPermission() {
        askReadStoragePermission.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}
