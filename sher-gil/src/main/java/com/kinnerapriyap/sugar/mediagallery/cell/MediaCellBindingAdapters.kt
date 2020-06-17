package com.kinnerapriyap.sugar.mediagallery.cell

import android.content.ContentResolver
import android.content.ContentUris
import android.content.res.Resources
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.CAMERA_CAPTURE_ID

@BindingAdapter("mediaUri")
fun ImageView.bindMediaUri(mediaUri: Uri) {
    val uri =
        if (ContentUris.parseId(mediaUri) == CAMERA_CAPTURE_ID) {
            getCameraDrawableUri(resources)
        } else mediaUri
    Glide.with(context)
        .load(uri)
        .into(this@bindMediaUri)
}

private fun getCameraDrawableUri(resources: Resources) =
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(resources.getResourcePackageName(R.drawable.ic_camera))
        .appendPath(resources.getResourceTypeName(R.drawable.ic_camera))
        .appendPath(resources.getResourceEntryName(R.drawable.ic_camera))
        .build()

@BindingAdapter("android:checked_state")
fun MaterialCardView.bindCheckedState(isChecked: Boolean) {
    this.isChecked = isChecked
}
