package com.kinnerapriyap.sugar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kinnerapriyap.sugar.GetMultipleFromGallery.Companion.RESULT_URIS
import java.util.ArrayList

class ShergilActivity : AppCompatActivity() {

    private val container: ImageView
        get() = findViewById(R.id.container)

    private val getMultipleFromGallery =
        registerForActivityResult(GetMultipleFromGallery()) { imageUriList ->
            val resultIntent =
                Intent().apply {
                    putParcelableArrayListExtra(RESULT_URIS, imageUriList as? ArrayList)
                }
            setResultAndFinish(resultIntent)
        }

    companion object {
        fun createIntent() = ShergilActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)

        getMultipleFromGallery.launch(
            GetMultipleFromGalleryInput(
                isOnlyFromLocal = false,
                isMultipleAllowed = true
            )
        )
    }

    private fun setResultAndFinish(result: Intent) {
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}
