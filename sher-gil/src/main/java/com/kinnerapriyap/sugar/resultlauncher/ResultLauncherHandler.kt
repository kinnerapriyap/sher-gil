package com.kinnerapriyap.sugar.resultlauncher

import androidx.activity.ComponentActivity

class ResultLauncherHandler(
    private val activity: ComponentActivity,
    private val setPermissionResult: (Boolean) -> Unit,
    private val setCameraCaptureResult: (Boolean) -> Unit
) : ResultLauncher by ResultLauncherImpl(
        activity.activityResultRegistry,
        activity,
        setPermissionResult,
        setCameraCaptureResult
    )
