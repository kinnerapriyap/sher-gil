package com.kinnerapriyap.sugar.mediagallery

import android.content.ContentResolver
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import com.kinnerapriyap.sugar.choice.MimeType
import com.kinnerapriyap.sugar.mediagallery.album.MediaGalleryAlbumCursorWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaGalleryHandler(private val contentResolver: ContentResolver) {

    /**
     *  In API 29, BUCKET_DISPLAY_NAME was moved to MediaStore.MediaColumns
     *  from MediaStore.Images.ImageColumns (which implements the former)
     *  However, the actual value of BUCKET_DISPLAY_NAME remains the sameA
     */

    companion object {
        const val ALBUM_MEDIA_COUNT = "album_media_count"

        /**
         *  Retrieve Data._ID to use while binding the result Cursor
         *  and Data.MIME_TYPE to identify each row data type
         */
        private val PROJECTION: Array<String> = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
        )

        private const val SELECTION =
            "${MediaStore.MediaColumns.MIME_TYPE} IN "

        private const val SORT_ORDER =
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
    }

    fun fetchAlbum(cursor: Cursor?): Cursor? {
        val extras = MatrixCursor(PROJECTION)
        extras.addRow(arrayOf("-1", MimeType.IMAGES, "All"))
        val cursors =
            arrayOf(extras, cursor)
        return MediaGalleryAlbumCursorWrapper(MergeCursor(cursors))
    }

    suspend fun fetchMedia(
        mimeTypes: List<MimeType>,
        showDisallowedMimeTypes: Boolean
    ): Cursor? =
        withContext(Dispatchers.IO) {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                if (showDisallowedMimeTypes) null else getSelection(mimeTypes),
                null,
                SORT_ORDER
            )
        }

    private fun getSelection(mimeTypes: List<MimeType>): String =
        SELECTION +
            mimeTypes.joinToString(
                prefix = "('",
                separator = "' , '",
                postfix = "')"
            ) { it.value }
}
