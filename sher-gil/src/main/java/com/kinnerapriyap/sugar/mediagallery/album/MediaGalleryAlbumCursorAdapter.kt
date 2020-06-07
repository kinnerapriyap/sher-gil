package com.kinnerapriyap.sugar.mediagallery.album

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.widget.SimpleCursorAdapter
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler

class MediaGalleryAlbumCursorAdapter(
    context: Context,
    cursor: Cursor?
) : SimpleCursorAdapter(
    context,
    R.layout.album_spinner_item,
    cursor,
    arrayOf(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, MediaGalleryHandler.ALBUM_MEDIA_COUNT),
    intArrayOf(R.id.albumName, R.id.mediaCount),
    0
) {

    override fun convertToString(cursor: Cursor?): CharSequence {
        val str = cursor?.getString(stringConversionColumn) as? CharSequence
        return if (str != "All" && !str.isNullOrBlank()) str
        else ""
    }

    override fun getStringConversionColumn(): Int =
        cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
}
