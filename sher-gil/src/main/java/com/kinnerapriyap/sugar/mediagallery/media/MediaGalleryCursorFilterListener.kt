package com.kinnerapriyap.sugar.mediagallery.media

import android.database.Cursor

interface MediaGalleryCursorFilterListener {
    fun fetchMediaAsync(constraint: CharSequence?): Cursor?
    fun getCursor(): Cursor?
    fun changeCursor(cursor: Cursor?)
}
