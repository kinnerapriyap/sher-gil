package com.kinnerapriyap.sugar.resultlauncher

import android.net.Uri

interface ResultLauncher {
    fun askReadStoragePermission()
    fun askWriteStorageAndCameraPermission()
    fun cameraCapture(uri: Uri?)
}
