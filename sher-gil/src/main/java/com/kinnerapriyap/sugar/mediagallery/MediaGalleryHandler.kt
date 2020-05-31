package com.kinnerapriyap.sugar.mediagallery

import android.content.ContentResolver
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore


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

        private val PROJECTION_ALBUM: Array<String> = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
        )

        /**
         * Searches for a album that matches the search string
         */
        private val SELECTION = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} = ?"

        private const val SORT_ORDER =
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
    }

    private fun getAlbumsCursor(): Cursor? =
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION_ALBUM,
            null,
            null,
            SORT_ORDER
        )

    fun fetchAlbum(): Cursor? {
        val extras = MatrixCursor(PROJECTION_ALBUM)
        extras.addRow(arrayOf("-1", "All"))
        val cursors =
            arrayOf(extras, getAlbumsCursor())
        return MergeCursor(cursors)
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