package com.kinnerapriyap.sugar.mediagallery

import android.database.Cursor

interface MediaGalleryCursorFilterListener {
    fun fetchMediaOnIO(constraint: CharSequence?): Cursor?
    fun getCursor(): Cursor?
    fun changeCursor(cursor: Cursor?)
}