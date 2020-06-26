package com.kinnerapriyap.sugar.mediagallery.cell

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.CAMERA_CAPTURE_ID

@BindingAdapter("mediaUri")
fun ImageView.bindMediaUri(mediaUri: Uri) {
    val uri =
        if (ContentUris.parseId(mediaUri) == CAMERA_CAPTURE_ID) {
            getCameraDrawableUri(resources)
        } else mediaUri
    val errorDrawable =
        getResourceFromAttr(R.attr.shergil_cardMediaErrorDrawable, context)
    val placeholderDrawable =
        getResourceFromAttr(R.attr.shergil_cardMediaPlaceholderDrawable, context)
    val requestOptions =
        RequestOptions().apply {
            errorDrawable?.let { error(it) }
            placeholderDrawable?.let { placeholder(it) }
        }
    Glide.with(context)
        .load(uri)
        .apply(requestOptions)
        .override(layoutParams.width, layoutParams.height)
        .thumbnail(0.1f)
        .into(this@bindMediaUri)
}

private fun getResourceFromAttr(attribute: Int, context: Context): Drawable? {
    var isValid: Boolean
    val typedValue = TypedValue().apply {
        isValid = context.theme.resolveAttribute(attribute, this, true)
    }
    return ContextCompat.getDrawable(
        context,
        if (isValid) typedValue.resourceId
        else R.drawable.ic_placeholder_media
    )
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
