package com.kinnerapriyap.sugar.mediagallery.cell

data class MediaCellUpdateModel(
    val positions: Pair<Int, Int>,
    val selectedMediaCellDisplayModels: List<MediaCellDisplayModel>
)
