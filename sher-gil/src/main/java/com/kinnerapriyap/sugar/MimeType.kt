package com.kinnerapriyap.sugar

/** Only supports images
 * Reference for MIME types supported by Android:
 * @see <a href="https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java/>
 */
enum class MimeType(s: String) {
    // Images
    JPEG("image/jpeg"),
    GIF("image/gif"),
    PNG("image/png"),
    BMP("image/x-ms-bmp"),
    HEIF("image/heif"),
    TIFF("image/tiff");

    companion object {
        // All supported image types
        val IMAGES: List<MimeType> = values().toList()
    }
}