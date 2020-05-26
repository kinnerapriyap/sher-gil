package com.kinnerapriyap.sugar.databinding

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView

@BindingAdapter("mediaUri")
fun ImageView.bindMediaUri(uri: Uri) {
    val bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source =
                ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    setImageBitmap(bitmap)
    // setImageURI(uri)
}

@BindingAdapter("android:checked_state")
fun MaterialCardView.bindCheckedState(isChecked: Boolean) {
    this.isChecked = isChecked
}