package com.kinnerapriyap.sugar.resultlauncher

import android.net.Uri

interface ResultLauncher {
    fun askReadStoragePermission()
    fun cameraCapture(uri: Uri?)
}
