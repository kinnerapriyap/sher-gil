package com.kinnerapriyap.sugar.databinding

import android.net.Uri
import android.view.View

interface MediaCellListener {
    fun onMediaCellClicked(view: View, uri: Uri)
}