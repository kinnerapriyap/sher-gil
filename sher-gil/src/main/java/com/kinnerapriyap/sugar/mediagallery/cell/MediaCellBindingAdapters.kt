package com.kinnerapriyap.sugar.mediagallery.cell

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

@BindingAdapter("mediaUri")
fun ImageView.bindMediaUri(uri: Uri) {
    Glide.with(context)
        .load(uri)
        .into(this@bindMediaUri)
}

@BindingAdapter("android:checked_state")
fun MaterialCardView.bindCheckedState(isChecked: Boolean) {
    this.isChecked = isChecked
}
