package com.kinnerapriyap.sugar.mediagallery

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.BaseColumns

class MediaGalleryHandler {

    /**
     *  In API 29, BUCKET_DISPLAY_NAME was moved to MediaStore.MediaColumns
     *  from MediaStore.Images.ImageColumns (which implements the former)
     *  However, the actual value of BUCKET_DISPLAY_NAME remains the sameA
     */

    private val selectionArgs: Array<String> = arrayOf("")

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

    fun fetchMedia(contentResolver: ContentResolver): List<Uri> {
        val images: MutableList<Uri> = mutableListOf()
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            null,
            null,
            SORT_ORDER
        )?.use { cursor ->
            /**
             * getColumnIndexOrThrow is used since _ID column exists in [BaseColumns]
             */
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val bucketDisplayNameColumn =
                cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                /**
                 * Get a URI representing the media item and
                 * append the id from the projection column to the base URI
                 */
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(idColumn)
                )
                images.add(contentUri)
            }
        }
        return images
    }
}