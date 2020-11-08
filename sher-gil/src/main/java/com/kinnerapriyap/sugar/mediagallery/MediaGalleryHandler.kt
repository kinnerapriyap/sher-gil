package com.kinnerapriyap.sugar.mediagallery

import android.content.ContentResolver
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.os.Build
import android.provider.MediaStore
import com.kinnerapriyap.sugar.choice.MimeType

class MediaGalleryHandler(private val contentResolver: ContentResolver) {

    companion object {
        const val ALL_ALBUM_BUCKET_DISPLAY_NAME = "All"
        const val CAMERA_CAPTURE_ID: Long = -3

        /**
         *  In API 29, BUCKET_DISPLAY_NAME was moved to MediaStore.MediaColumns
         *  from MediaStore.Images.ImageColumns (which implements the former)
         *  However, the actual value of BUCKET_DISPLAY_NAME remains the same
         */
        val BUCKET_DISPLAY_NAME =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
            } else "bucket_display_name"

        /**
         *  Retrieve Data._ID to use while binding the result Cursor
         *  and Data.MIME_TYPE to identify each row data type
         */
        private val PROJECTION: Array<String> = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            BUCKET_DISPLAY_NAME
        )

        private const val SELECTION =
            "${MediaStore.MediaColumns.MIME_TYPE} IN "

        private const val SORT_ORDER =
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
    }

    fun fetchMedia(
        mimeTypes: List<MimeType>,
        showDisallowedMimeTypes: Boolean,
        allowCamera: Boolean
    ): Cursor? {
        val extras = MatrixCursor(PROJECTION)
        if (allowCamera) {
            extras.addRow(
                arrayOf(
                    CAMERA_CAPTURE_ID.toString(),
                    MimeType.IMAGES,
                    ALL_ALBUM_BUCKET_DISPLAY_NAME
                )
            )
        }
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            if (showDisallowedMimeTypes) null else getSelection(mimeTypes),
            null,
            SORT_ORDER
        )
        return MergeCursor(arrayOf(extras, cursor))
    }

    private fun getSelection(mimeTypes: List<MimeType>): String =
        SELECTION +
            mimeTypes.joinToString(
                prefix = "('",
                separator = "' , '",
                postfix = "')"
            ) { it.value }
}
