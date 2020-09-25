package com.kinnerapriyap.sugar.mediagallery.album

import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.ALL_ALBUM_BUCKET_DISPLAY_NAME

data class MediaGalleryAlbum(
    val albumName: String,
    val mediaCount: Int
)

fun MediaGalleryAlbum.toBucketDisplayName() =
    if (albumName != ALL_ALBUM_BUCKET_DISPLAY_NAME) albumName else ""
