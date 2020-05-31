package com.kinnerapriyap.sugar.mediagallery

import android.database.Cursor
import android.database.CursorWrapper
import android.provider.MediaStore

class MediaGalleryAlbumCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {

    private val origCount = super.getCount()
    private var filterMap = IntArray(origCount)
    private var fPosition = -2
    private var fCount = 0
    private var addedNames: MutableList<String> = mutableListOf()

    init {
        for (i in 0 until origCount) {
            super.moveToPosition(i)
            val name = getString(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
            if (!addedNames.contains(name)) {
                filterMap[fCount++] = i
                addedNames.add(name)
            }
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
        moveToPosition(-1)

    override fun moveToLast(): Boolean =
        moveToPosition(count - 1)

    override fun moveToNext(): Boolean =
        moveToPosition(fPosition + 1)

    override fun moveToPrevious(): Boolean =
        moveToPosition(fPosition - 1)

    override fun isFirst(): Boolean =
        fPosition == -1 && count != 0

    override fun isLast(): Boolean =
        fPosition == count - 1 && count != 0

    override fun isBeforeFirst(): Boolean =
        if (count == 0) true
        else fPosition == -1

    override fun isAfterLast(): Boolean =
        if (count == 0) true
        else fPosition == count

    override fun getPosition(): Int = fPosition
}