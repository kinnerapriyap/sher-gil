package com.kinnerapriyap.sugar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.fragment.app.Fragment
import com.kinnerapriyap.sugar.ShergilActivity.Companion.RESULT_URIS
import com.kinnerapriyap.sugar.choice.ChoiceBuilder

open class Shergil {

    companion object {
        /**
         * Open Shergil from an Activity
         * Handle result in [Activity.onActivityResult] once user finishes selection
         *
         * @param activity Activity instance
         * @return [ChoiceBuilder] instance
         */
        fun create(activity: Activity) =
            ChoiceBuilder(activity)

        /**
         * Open Shergil from a Fragment
         * Handle result in [Fragment.onActivityResult] or in the
         * [ActivityResultCallback.onActivityResult] callback used with
         * [Fragment.registerForActivityResult] once user finishes selection
         * @see <a href="https://developer.android.com/training/basics/intents/result"/>
         *
         * @param fragment Fragment instance
         * @return [ChoiceBuilder] instance
         */
        fun create(fragment: Fragment) =
            ChoiceBuilder(fragment)

        /**
         * Get list of [Uri] for selected media
         *
         * @param data Intent from [Activity.onActivityResult] or [Fragment.onActivityResult]
         * or [ActivityResultCallback.onActivityResult]
         * @return List<Uri> for selected media or emptyList if none were selected
         */
        fun getMediaUris(data: Intent?): List<Uri> =
            data?.getParcelableArrayListExtra(RESULT_URIS) ?: emptyList()
    }
}
