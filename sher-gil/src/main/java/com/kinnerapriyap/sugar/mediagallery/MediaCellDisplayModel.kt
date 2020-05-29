package com.kinnerapriyap.sugar.mediagallery

import android.net.Uri

data class MediaCellDisplayModel(
    val position: Int,
    val mediaUri: Uri,
    val isChecked: Boolean = false,
    val bucketDisplayName: String
)
