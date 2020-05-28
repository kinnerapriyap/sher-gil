package com.kinnerapriyap.sugar.extension

import android.net.Uri
import com.kinnerapriyap.sugar.mediagallery.MediaCellDisplayModel

fun Uri.toMediaCellDisplayModel(isChecked: Boolean) =
    MediaCellDisplayModel(
        mediaUri = this,
        isChecked = isChecked
    )