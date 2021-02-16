package com.kinnerapriyap.sugar.choice

/**
 * Currently only supports images
 * Reference for MIME types supported by Android:
 * See [MediaFile.java](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java/)
 */
@Suppress("unused")
enum class MimeType(val value: String) {
    // Images
    JPEG("image/jpeg"),
    JPG("image/jpg"),
    GIF("image/gif"),
    PNG("image/png"),
    BMP("image/x-ms-bmp"),
    HEIF("image/heif"),
    TIFF("image/tiff");

    companion object {
        // All supported image types
        val IMAGES: List<MimeType> = values().toList()

        private val map = values().associateBy(MimeType::value)
        fun fromValue(value: String?): MimeType? = map.getOrDefault(value, null)
    }
}
