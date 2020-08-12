package com.kinnerapriyap.sugar.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import java.text.SimpleDateFormat
import java.util.Locale

const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

fun getFileDisplayName(format: String): String =
    SimpleDateFormat(format, Locale.ENGLISH)
        .format(System.currentTimeMillis())

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
