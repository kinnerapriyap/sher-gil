package com.kinnerapriyap.sugar

import com.airbnb.epoxy.EpoxyController
import com.kinnerapriyap.sugar.databinding.MediaCellDisplayModel
import com.kinnerapriyap.sugar.databinding.MediaCellListener

class ShergilController(
    private val listener: MediaCellListener
) : EpoxyController() {

    var mediaCellDisplayModels: List<MediaCellDisplayModel> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        mediaCellDisplayModels.forEach { displayModel ->
            mediaCell {
                id(displayModel.mediaUri.path)
                displayModel(displayModel)
                listener(listener)
            }
        }
    }
}