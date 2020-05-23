package com.kinnerapriyap.sugar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

class ShergilActivity : AppCompatActivity() {

    private val container: ImageView
        get() = findViewById(R.id.container)

    companion object {
        private const val RESULT_URIS = "result_uris"
        private const val GALLERY_REQUEST = 4541
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)

        startActivityForResult(getGalleryIntent(), GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = Intent()
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST) {
            val images = mutableListOf<Uri>().apply {
                data?.clipData?.let {
                    for (i in 0 until it.itemCount) {
                        add(it.getItemAt(i).uri)
                    }
                }
            }
            result.apply {
                putParcelableArrayListExtra(RESULT_URIS, images as? ArrayList)
            }
        }
        setResultAndFinish(result)
    }

    private fun setResultAndFinish(result: Intent) {
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun getGalleryIntent() =
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
}
