package com.kinnerapriyap.sugar

import android.net.Uri
import com.airbnb.epoxy.EpoxyController
import com.kinnerapriyap.sugar.databinding.MediaCellListener

class ShergilController(
    private val listener: MediaCellListener
) : EpoxyController() {

    private var mediaList: Map<Uri, Boolean> = emptyMap()

    fun setMediaList(mediaList: Map<Uri, Boolean>) {
        this.mediaList = mediaList
        requestModelBuild()
    }

    override fun buildModels() {
        mediaList.forEach { (uri, isChecked) ->
            mediaCell {
                id(uri.path)
                mediaUri(uri)
                isChecked(isChecked)
                listener(listener)
            }
        }
    }
}