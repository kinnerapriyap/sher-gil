package com.kinnerapriyap.sugar.camera

interface CameraFragmentListener {
    fun hideBars()
    fun showBars()
    fun openMediaGallery()
    fun setResultCancelledAndFinish()
    fun onCameraCaptureYesClicked()
}
