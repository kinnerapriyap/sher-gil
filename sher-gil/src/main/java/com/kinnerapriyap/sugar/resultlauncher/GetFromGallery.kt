package com.kinnerapriyap.sugar.resultlauncher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.kinnerapriyap.sugar.MimeType

data class GetFromGalleryInput(
    val mimeTypes: List<MimeType> = MimeType.IMAGES,
    val allowOnlyLocalStorage: Boolean = false,
    val allowMultipleSelection: Boolean = true
)

class GetMultipleFromGallery : ActivityResultContract<GetFromGalleryInput, List<Uri>>() {

    companion object {
        const val RESULT_URIS = "result_uris"
    }

    override fun createIntent(context: Context, input: GetFromGalleryInput): Intent =
        Intent(Intent.ACTION_GET_CONTENT)
            .apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, input.allowMultipleSelection)
                putExtra(Intent.EXTRA_LOCAL_ONLY, input.allowOnlyLocalStorage)
            }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> =
        when (resultCode) {
            Activity.RESULT_OK ->
                mutableListOf<Uri>().apply {
                    intent?.clipData?.let {
                        for (i in 0 until it.itemCount) {
                            add(it.getItemAt(i).uri)
                        }
                    }
                }
            else -> emptyList()
        }
}