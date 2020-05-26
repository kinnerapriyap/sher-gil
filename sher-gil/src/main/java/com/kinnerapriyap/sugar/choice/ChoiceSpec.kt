package com.kinnerapriyap.sugar.choice

class ChoiceSpec private constructor() {
    var mimeTypes: List<MimeType> = MimeType.IMAGES
    var allowOnlyLocalStorage: Boolean = false
    var allowMultipleSelection: Boolean = true
    var numOfColumns: Int = 2

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
        allowOnlyLocalStorage = false
        allowMultipleSelection = true
        numOfColumns = 2
    }
}
