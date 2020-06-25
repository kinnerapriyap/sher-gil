package com.kinnerapriyap.sugar.resultlauncher

import androidx.activity.ComponentActivity

class ResultLauncherHandler(
    private val activity: ComponentActivity,
    private val setReadStoragePermissionResult: (Boolean) -> Unit,
    private val setCameraPermissionResult: (Boolean) -> Unit
) : ResultLauncher by ResultLauncherImpl(
        activity.activityResultRegistry,
        activity,
        setReadStoragePermissionResult,
        setCameraPermissionResult
    )
