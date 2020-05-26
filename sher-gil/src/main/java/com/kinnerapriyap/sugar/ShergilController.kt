package com.kinnerapriyap.sugar

import android.net.Uri
import com.airbnb.epoxy.EpoxyController

class ShergilController : EpoxyController() {

    private var mediaList: List<Uri> = emptyList()

    fun setMediaList(mediaList: List<Uri>) {
        this.mediaList = mediaList
        requestModelBuild()
    }

    override fun buildModels() {
        mediaList.forEach { uri ->
            cell {
                id(uri.path)
                mediaUri(uri)
            }
        }
    }
}