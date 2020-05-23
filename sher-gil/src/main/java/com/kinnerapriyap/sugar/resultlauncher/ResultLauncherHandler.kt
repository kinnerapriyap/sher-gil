package com.kinnerapriyap.sugar.resultlauncher

import android.net.Uri
import androidx.activity.ComponentActivity

class ResultLauncherHandler(
    private val activity: ComponentActivity,
    private val setGalleryResult: ((List<Uri>) -> Unit),
    private val setPermissionResult: (Boolean) -> Unit
) : ResultLauncher by ResultLauncherImpl(
    activity.activityResultRegistry,
    activity,
    setGalleryResult,
    setPermissionResult
)