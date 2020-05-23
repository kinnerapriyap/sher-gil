package com.kinnerapriyap.sugar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ShergilActivity : AppCompatActivity() {

    private val container: ImageView
        get() = findViewById(R.id.container)

    companion object {
        private const val GALLERY_REQUEST = 4541
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)

        startActivityForResult(getGalleryIntent(), GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val image = data?.data?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else MediaStore.Images.Media.getBitmap(contentResolver, it)
            } as? Bitmap
            Log.e("kin", image.toString())
            container.setImageBitmap(image)
        }
    }

    private fun getGalleryIntent() =
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
}
