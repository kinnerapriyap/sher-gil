package com.kinnerapriyap.sugar.choice

import com.kinnerapriyap.sugar.R

class ChoiceSpec private constructor() {
    var mimeTypes: List<MimeType> = MimeType.IMAGES
    var showDisallowedMimeTypes: Boolean = false
    var allowOnlyLocalStorage: Boolean = false
    var allowMultipleSelection: Boolean = true
    var numOfColumns: Int = 2
    var theme: Int = R.style.Shergil

    private object InstanceHolder {
        val INSTANCE = ChoiceSpec()
    }

    companion object {
        val instance: ChoiceSpec
            get() = InstanceHolder.INSTANCE

        val cleanInstance: ChoiceSpec
            get() {
                val selectionSpec = instance
                selectionSpec.reset()
                return selectionSpec
            }
    }

    private fun reset() {
        mimeTypes = MimeType.IMAGES
        showDisallowedMimeTypes = false
        allowOnlyLocalStorage = false
        allowMultipleSelection = true
        numOfColumns = 2
        theme = R.style.Shergil
    }
}
