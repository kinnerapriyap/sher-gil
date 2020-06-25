package com.kinnerapriyap.sugar.camera

import androidx.camera.core.ImageCapture
import com.kinnerapriyap.sugar.R

enum class CameraFlash(
    val flashMode: Int,
    val flashDrawable: Int
) {
    FLASH_OFF(ImageCapture.FLASH_MODE_OFF, R.drawable.ic_flash_off),
    FLASH_AUTO(ImageCapture.FLASH_MODE_AUTO, R.drawable.ic_flash_auto),
    FLASH_ON(ImageCapture.FLASH_MODE_ON, R.drawable.ic_flash_on)
}
