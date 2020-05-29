package com.kinnerapriyap.sugar.mediagallery

import android.database.Cursor
import android.widget.Filter

class MediaGalleryCursorFilter(
    private val listener: MediaGalleryCursorFilterListener
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val cursor = listener.fetchMediaOnIO(constraint)
        return FilterResults().apply {
            count = cursor?.count ?: 0
            values = cursor
        }
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        val oldCursor = listener.getCursor()
        val resultValues = results?.values ?: return
        if (resultValues !== oldCursor) {
            listener.changeCursor(resultValues as? Cursor)
        }
    }
}