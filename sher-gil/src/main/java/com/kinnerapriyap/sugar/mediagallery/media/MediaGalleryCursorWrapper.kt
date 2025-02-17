package com.kinnerapriyap.sugar.mediagallery.media

import android.database.Cursor
import android.database.CursorWrapper
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler

class MediaGalleryCursorWrapper(
    cursor: Cursor?,
    bucketDisplayName: String?
) : CursorWrapper(cursor) {

    private val origCount = super.getCount()
    private var filterMap = IntArray(origCount)
    private var fPosition = -1
    private var fCount = 0

    init {
        if (bucketDisplayName.isNullOrBlank()) {
            fCount = origCount
            for (i in 0 until count) {
                filterMap[i] = i
            }
        } else {
            for (i in 0 until origCount) {
                super.moveToPosition(i)
                val name =
                    getString(getColumnIndexOrThrow(MediaGalleryHandler.BUCKET_DISPLAY_NAME))
                if (name == bucketDisplayName) {
                    filterMap[fCount++] = i
                }
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
}
