package com.kinnerapriyap.sugar

/**
 * See [SingleLiveEvent](https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150)
 */
open class SingleLiveEvent<out T>(private val content: T) {
    /**
     * Used as a wrapper for data that is exposed via a LiveData that represents an event.
     */
    var hasBeenHandled = false
        private set

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}