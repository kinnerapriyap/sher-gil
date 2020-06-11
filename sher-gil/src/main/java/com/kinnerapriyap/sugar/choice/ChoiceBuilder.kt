package com.kinnerapriyap.sugar.choice

import android.app.Activity
import android.content.Intent
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
     * Determines whether or not to allow multiple media selection
     *
     * @param allowMultipleSelection Boolean default is true
     * @return [ChoiceBuilder] instance
     */
    fun allowMultipleSelection(allowMultipleSelection: Boolean): ChoiceBuilder =
        apply {
            choiceSpec.allowMultipleSelection = allowMultipleSelection
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
