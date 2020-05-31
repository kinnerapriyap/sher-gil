package com.kinnerapriyap.sugar.mediagallery

import android.content.ContentResolver
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import com.kinnerapriyap.sugar.choice.MimeType
import com.kinnerapriyap.sugar.mediagallery.album.MediaGalleryAlbumCursorWrapper


class MediaGalleryHandler(private val contentResolver: ContentResolver) {

    /**
     *  In API 29, BUCKET_DISPLAY_NAME was moved to MediaStore.MediaColumns
     *  from MediaStore.Images.ImageColumns (which implements the former)
     *  However, the actual value of BUCKET_DISPLAY_NAME remains the sameA
     */

    companion object {
        /**
         *  Retrieve Data._ID to use while binding the result Cursor
         *  and Data.MIMETYPE to identify each row data type
         */
        private val PROJECTION: Array<String> = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
        )

        private const val SORT_ORDER =
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
    }

    fun fetchAlbum(cursor: Cursor?): Cursor? {
        val extras = MatrixCursor(PROJECTION)
        extras.addRow(arrayOf("-1", MimeType.IMAGES ,"All"))
        val cursors =
            arrayOf(extras, cursor)
        return MediaGalleryAlbumCursorWrapper(MergeCursor(cursors))
    }

    fun fetchMedia(): Cursor? =
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            null,
            null,
            SORT_ORDER
        )
}