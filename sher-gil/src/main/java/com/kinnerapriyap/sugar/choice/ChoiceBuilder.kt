package com.kinnerapriyap.sugar.choice

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.ShergilActivity

class ChoiceBuilder private constructor(
    private val activity: Activity? = null,
    private val fragment: Fragment? = null,
    private var choiceSpec: ChoiceSpec = ChoiceSpec.cleanInstance
) {

    constructor(activity: Activity) : this(activity, null)
    constructor(fragment: Fragment) : this(fragment.activity, fragment)

    /**
     * Selects the MIME types allowed to be chosen by the user
     * MIME types not included will be shown but
     * cannot be chosen if showDisallowedMimeTypes is true
     *
     * @param mimeTypes  List<[MimeType]> default is MimeType.IMAGES (all image types)
     * @return [ChoiceBuilder] instance
     */
    fun mimeTypes(mimeTypes: List<MimeType>): ChoiceBuilder =
        apply {
            choiceSpec.mimeTypes = mimeTypes
        }

    /**
     * Determines whether or not to show the MIME types
     * not allowed to be chosen by the user
     *
     * @param showDisallowedMimeTypes Boolean default is false
     * @return [ChoiceBuilder] instance
     */
    fun showDisallowedMimeTypes(showDisallowedMimeTypes: Boolean): ChoiceBuilder =
        apply {
            choiceSpec.showDisallowedMimeTypes = showDisallowedMimeTypes
        }

    /**
     * Determines whether or not to allow media selection from local storage only
     *
     * @param allowOnlyLocalStorage Boolean default is false
     * @return [ChoiceBuilder] instance
     */
    fun allowOnlyLocalStorage(allowOnlyLocalStorage: Boolean): ChoiceBuilder =
        apply {
            choiceSpec.allowOnlyLocalStorage = allowOnlyLocalStorage
        }

    /**
     * Determines the number of columns in which media is displayed
     *
     * @param numOfColumns Int default is 2
     * @return [ChoiceBuilder] instance
     */
    fun numOfColumns(numOfColumns: Int): ChoiceBuilder =
        apply {
            choiceSpec.numOfColumns = numOfColumns
        }

    /**
     * Sets the custom theme for Shergil
     *
     * @param theme Int default is [R.style.Shergil]
     * @return [ChoiceBuilder] instance
     */
    fun theme(theme: Int): ChoiceBuilder =
        apply {
            choiceSpec.theme = theme
        }

    /**
     * Determines whether or not to allow selected media preview
     *
     * @param allowPreview Boolean default is true
     * @return [ChoiceBuilder] instance
     */
    fun allowPreview(allowPreview: Boolean): ChoiceBuilder =
        apply {
            choiceSpec.allowPreview = allowPreview
        }

    /**
     * Sets the maximum limit for media selection
     * Throws IllegalArgumentException
     * if the value passed is not greater than 0
     *
     * @param maxSelectable Int default is [Integer.MAX_VALUE]
     * @return [ChoiceBuilder] instance
     */
    fun maxSelectable(maxSelectable: Int): ChoiceBuilder =
        apply {
            if (maxSelectable <= 0)
                throw IllegalArgumentException("maxSelectable must be greater than 0")
            choiceSpec.maxSelectable = maxSelectable
        }

    /**
     * Determines whether or not to allow camera capture
     *
     * @param allowCamera Boolean default is true
     * @return [ChoiceBuilder] instance
     */
    fun allowCamera(allowCamera: Boolean): ChoiceBuilder =
        apply {
            choiceSpec.allowCamera = allowCamera
        }

    /**
     * Determines whether or not to show the device camera when allowCamera is true
     * If showDeviceCamera is false, library implementation of camera is shown
     *
     * @param showDeviceCamera Boolean default is false
     * @return [ChoiceBuilder] instance
     */
    fun showDeviceCamera(showDeviceCamera: Boolean): ChoiceBuilder =
        apply {
            choiceSpec.showDeviceCamera = showDeviceCamera
        }

    /**
     * Determines whether or not to show camera first when allowCamera is true
     * If showCameraFirst or allowCamera are false, gallery is opened first
     *
     * @param showCameraFirst Boolean default is false
     * @return [ChoiceBuilder] instance
     */
    fun showCameraFirst(showCameraFirst: Boolean): ChoiceBuilder =
        apply {
            choiceSpec.showCameraFirst = showCameraFirst
        }

    /**
     * Sets or restricts orientation
     *
     * @param orientation Int default is [ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED]
     * @return [ChoiceBuilder] instance
     */
    fun orientation(orientation: Int): ChoiceBuilder =
        apply {
            choiceSpec.orientation = orientation
        }

    /**
     * Sets the maximum scale factor for zoom in preview
     * Zoom will be disabled in preview if value is set to 1f
     *
     * @param previewMaxScaleFactor Float default is 5f
     * @return [ChoiceBuilder] instance
     */
    fun previewMaxScaleFactor(previewMaxScaleFactor: Float): ChoiceBuilder =
        apply {
            choiceSpec.previewMaxScaleFactor = previewMaxScaleFactor
        }

    /**
     * Opens media selection with the requestCode provided
     *
     * @param requestCode Request code to be returned in onActivityResult
     */
    fun withRequestCode(requestCode: Int) {
        val activity = activity ?: return
        val intent = Intent(activity, ShergilActivity::class.java)
        with(fragment) {
            if (this != null) this.startActivityForResult(intent, requestCode)
            else activity.startActivityForResult(intent, requestCode)
        }
    }
}
