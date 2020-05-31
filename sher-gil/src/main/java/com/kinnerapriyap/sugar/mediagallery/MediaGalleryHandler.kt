package com.kinnerapriyap.sugar.mediagallery

import android.content.ContentResolver
import android.database.Cursor
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

        /**
         * Searches for a album that matches the search string
         */
        private val SELECTION = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} = ?"

        private const val SORT_ORDER =
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
    }

    fun fetchMediaByAlbum(bucketDisplayName: String): Cursor? =
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            SELECTION,
            arrayOf(bucketDisplayName),
            SORT_ORDER
        )

    fun fetchMedia(): Cursor? =
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            null,
            null,
            SORT_ORDER
        )
}