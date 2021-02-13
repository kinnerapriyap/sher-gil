package com.kinnerapriyap.sugar.mediagallery.cell

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import com.kinnerapriyap.sugar.choice.MimeType
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MediaCellDisplayModel(
    val position: Int,
    val id: Long,
    val mediaUri: Uri,
    val isChecked: Boolean = false,
    val isEnabled: Boolean,
    val bucketDisplayName: String?,
    val mimeType: MimeType?
) : Parcelable
