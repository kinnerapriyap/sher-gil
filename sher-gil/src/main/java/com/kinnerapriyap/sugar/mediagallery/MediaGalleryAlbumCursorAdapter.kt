package com.kinnerapriyap.sugar.mediagallery

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.widget.SimpleCursorAdapter

class MediaGalleryAlbumCursorAdapter(
    context: Context,
    cursor: Cursor?
) : SimpleCursorAdapter(
    context,
    android.R.layout.simple_spinner_item,
    cursor,
    arrayOf(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME),
    intArrayOf(android.R.id.text1),
    0
) {

    override fun convertToString(cursor: Cursor?): CharSequence =
        cursor?.getString(stringConversionColumn) as? CharSequence ?: ""

    override fun getStringConversionColumn(): Int =
        cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
}