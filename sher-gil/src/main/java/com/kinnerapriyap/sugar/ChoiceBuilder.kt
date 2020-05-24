package com.kinnerapriyap.sugar

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kinnerapriyap.sugar.resultlauncher.GetFromGalleryInput

class ChoiceBuilder private constructor(
    private val activity: Activity? = null,
    private val fragment: Fragment? = null,
    private var mimeTypes: List<MimeType> = MimeType.IMAGES,
    private var getFromGalleryInput: GetFromGalleryInput = GetFromGalleryInput()
) {

    constructor(activity: Activity) : this(activity, null)
    constructor(fragment: Fragment) : this(fragment.activity, fragment)

    /**
     * Selects the MIME types allowed to be chosen by the user
     * MIME types not included will be shown but cannot be chosen
     *
     * @param mimeTypes  List<[MimeType]> default is MimeType.IMAGES (all image types)
     * @return [ChoiceBuilder] instance
     */
    fun mimeTypes(mimeTypes: List<MimeType>): ChoiceBuilder =
        apply {
            this.mimeTypes = mimeTypes
        }

    /**
     * Determines whether or not to allow media selection from local storage only
     *
     * @param allowOnlyLocalStorage Boolean default is false
     * @return [ChoiceBuilder] instance
     */
    fun allowOnlyLocalStorage(allowOnlyLocalStorage: Boolean): ChoiceBuilder =
        apply {
            this.getFromGalleryInput = this.getFromGalleryInput.copy(
                allowOnlyLocalStorage = allowOnlyLocalStorage
            )
        }

    /**
     * Determines whether or not to allow multiple media selection
     *
     * @param allowMultiple Boolean default is true
     * @return [ChoiceBuilder] instance
     */
    //TODO: Rename to allowMultipleSelection
    fun allowMultiple(allowMultiple: Boolean): ChoiceBuilder =
        apply {
            this.getFromGalleryInput = this.getFromGalleryInput.copy(
                allowMultiple = allowMultiple
            )
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