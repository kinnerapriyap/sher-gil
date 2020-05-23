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
    private val registry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    private val setGalleryResult: ((List<Uri>) -> Unit),
    private val setPermissionResult: (Boolean) -> Unit
) : LifecycleObserver, ResultLauncher {

    private lateinit var getMultipleFromGallery: ActivityResultLauncher<GetMultipleFromGalleryInput>

    private lateinit var askReadStoragePermission: ActivityResultLauncher<String>

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        initialiseResultLaunchers()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getMultipleFromGallery.unregister()
        askReadStoragePermission.unregister()
    }

    private fun initialiseResultLaunchers() {
        getMultipleFromGallery =
            registry.register(
                "key",
                GetMultipleFromGallery()
            ) { imageUriList ->
                setGalleryResult(imageUriList)
            }

        askReadStoragePermission =
            registry.register(
                "key",
                ActivityResultContracts.RequestPermission()
            ) { allowed ->
                setPermissionResult(allowed)
            }
    }

    override fun askPermission() {
        askReadStoragePermission.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun openGallery() {
        getMultipleFromGallery.launch(
            GetMultipleFromGalleryInput(
                allowOnlyLocalStorage = false,
                allowMultiple = true
            )
        )
    }
}