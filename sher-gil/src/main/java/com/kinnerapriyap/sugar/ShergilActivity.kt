package com.kinnerapriyap.sugar

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
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

    private val askReadStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
            if (allowed) openGallery()
            else setCancelledAndFinish()
        }

    companion object {
        fun createIntent() = ShergilActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ->
                askReadStoragePermission.launch(READ_EXTERNAL_STORAGE)
            else ->
                openGallery()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getMultipleFromGallery.unregister()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun setResultAndFinish(result: Intent) {
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun setCancelledAndFinish() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun openGallery() {
        getMultipleFromGallery.launch(
            GetMultipleFromGalleryInput(
                isOnlyFromLocal = false,
                isMultipleAllowed = true
            )
        )
    }
}
