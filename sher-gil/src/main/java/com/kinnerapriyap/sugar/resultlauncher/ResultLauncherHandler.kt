package com.kinnerapriyap.sugar.resultlauncher

import androidx.activity.ComponentActivity

class ResultLauncherHandler(
    private val activity: ComponentActivity,
    private val setReadStoragePermissionResult: (Boolean) -> Unit,
    private val setWriteStorageAndCameraPermissionResult: (Map<String, Boolean>) -> Unit,
    private val setCameraCaptureResult: (Boolean) -> Unit
) : ResultLauncher by ResultLauncherImpl(
        activity.activityResultRegistry,
        activity,
        setReadStoragePermissionResult,
        setWriteStorageAndCameraPermissionResult,
        setCameraCaptureResult
    )
