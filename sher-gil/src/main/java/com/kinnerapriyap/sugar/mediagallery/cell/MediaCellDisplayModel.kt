package com.kinnerapriyap.sugar.mediagallery.cell

import android.net.Uri
import com.kinnerapriyap.sugar.choice.MimeType

data class MediaCellDisplayModel(
    val position: Int,
    val mediaUri: Uri,
    val isChecked: Boolean = false,
    val bucketDisplayName: String,
    val mimeType: MimeType?
)
