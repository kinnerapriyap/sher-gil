package com.kinnerapriyap.sugar.mediagallery.album

import android.database.Cursor
import android.database.CursorWrapper
import android.provider.MediaStore
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.ALBUM_MEDIA_COUNT

class MediaGalleryAlbumCursorWrapper(
    cursor: Cursor?,
    allowCamera: Boolean
) : CursorWrapper(cursor) {

    private val origCount = super.getCount()
    private var filterMap = IntArray(origCount)
    private var fPosition = -1
    private var fCount = 0
    private var addedNames: MutableList<String> = mutableListOf()
    private var addedNamesCount: MutableMap<String, Int> = mutableMapOf()

    init {
        for (i in 0 until origCount) {
            super.moveToPosition(i)
            val name: String = getString(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
                    ?: continue
            if (!addedNames.contains(name)) {
                addedNamesCount[name] = 1
                filterMap[fCount++] = i
                addedNames.add(name)
            } else {
                addedNamesCount[name] = (addedNamesCount[name] ?: 0) + 1
            }
        }
        /**
         * origCount - 1 is used for All count since the inserted row is not to be counted
         */
        addedNamesCount["All"] = origCount - 1
        if (allowCamera) {
            addedNamesCount["All"] = addedNamesCount["All"]?.minus(1) ?: 0
        }
        moveToFirst()
    }

    override fun getCount(): Int = fCount

    override fun moveToPosition(position: Int): Boolean {
        /**
         * Check that position is not past the end of the cursor
         * or before the beginning of the cursor
         */
        if (position >= this.count || position < -1) return false
        fPosition = position
        if (position == -1) return false
        return super.moveToPosition(filterMap[position])
    }

    override fun move(offset: Int): Boolean =
        moveToPosition(fPosition + offset)

    override fun moveToFirst(): Boolean =
        moveToPosition(0)

    override fun moveToLast(): Boolean =
        moveToPosition(count - 1)

    override fun moveToNext(): Boolean =
        moveToPosition(fPosition + 1)

    override fun moveToPrevious(): Boolean =
        moveToPosition(fPosition - 1)

    override fun isFirst(): Boolean =
        fPosition == 0 && count != 0

    override fun isLast(): Boolean =
        fPosition == count - 1 && count != 0

    override fun isBeforeFirst(): Boolean =
        if (count == 0) true
        else fPosition == -1

    override fun isAfterLast(): Boolean =
        if (count == 0) true
        else fPosition == count

    override fun getPosition(): Int = fPosition

    // Add column for media count

    private val origColumnCount = super.getColumnCount()

    override fun getColumnCount(): Int =
        origColumnCount + 1

    override fun getColumnIndex(columnName: String?): Int =
        if (columnName != null && columnName == ALBUM_MEDIA_COUNT) {
            origColumnCount
        } else {
            wrappedCursor.getColumnIndex(columnName)
        }

    @Throws(IllegalArgumentException::class)
    override fun getColumnIndexOrThrow(columnName: String?): Int =
        if (columnName != null && columnName == ALBUM_MEDIA_COUNT) {
            origColumnCount
        } else {
            wrappedCursor.getColumnIndexOrThrow(columnName)
        }

    override fun getColumnName(columnIndex: Int): String =
        if (columnIndex == origColumnCount) {
            ALBUM_MEDIA_COUNT
        } else {
            wrappedCursor.getColumnName(columnIndex)
        }

    override fun getColumnNames(): Array<String> {
        val result = wrappedCursor.columnNames.toMutableList()
        result.add(origColumnCount, ALBUM_MEDIA_COUNT)
        return result.toTypedArray()
    }

    override fun getString(columnIndex: Int): String? {
        if (columnIndex == origColumnCount) {
            val name = getString(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
            return addedNamesCount[name].toString()
        }
        return wrappedCursor.getString(columnIndex)
    }
}
