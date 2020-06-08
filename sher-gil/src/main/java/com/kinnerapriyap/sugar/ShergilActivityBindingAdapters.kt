package com.kinnerapriyap.sugar

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("selectedCount")
fun TextView.bindSelectedCount(selectedCount: Int) {
    isEnabled = selectedCount != 0
    text = StringBuilder().apply {
        append(resources.getString(R.string.apply))
        if (selectedCount != 0) {
            append(" ($selectedCount)")
        }
    }
}
