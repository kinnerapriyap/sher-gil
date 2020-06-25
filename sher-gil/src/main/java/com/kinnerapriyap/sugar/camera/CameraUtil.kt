package com.kinnerapriyap.sugar.camera

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import com.kinnerapriyap.sugar.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

fun getOutputDirectory(activity: Activity): File {
    val dirName = getStringResourceFromAttr(R.attr.shergil_appName, activity)
    val mediaDir =
        activity.externalMediaDirs.firstOrNull()?.let {
            File(it, dirName).apply { mkdirs() }
        }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else activity.filesDir
}

fun createFile(baseFolder: File, format: String, extension: String) =
    File(
        baseFolder,
        SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension
    )

private fun getStringResourceFromAttr(attribute: Int, context: Context): String {
    var isValid: Boolean
    val typedValue = TypedValue().apply {
        isValid = context.theme.resolveAttribute(attribute, this, true)
    }
    return context.resources.getString(
        if (isValid) typedValue.resourceId
        else R.string.shergil
    )
}

/**
 * Determines whether or not the device has an available back camera
 */
fun ProcessCameraProvider?.hasBackCamera(): Boolean =
    this?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

/**
 * Determines whether or not the device has an available front camera
 */
fun ProcessCameraProvider?.hasFrontCamera(): Boolean =
    this?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
