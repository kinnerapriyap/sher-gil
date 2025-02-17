package com.kinnerapriyap.sample

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kinnerapriyap.sugar.Shergil
import com.kinnerapriyap.sugar.choice.MimeType

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_SHERGIL = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "sample"

        findViewById<TextView>(R.id.hello).setOnClickListener {
            Shergil.create(this)
                .mimeTypes(MimeType.IMAGES)
                .showDisallowedMimeTypes(true)
                .allowOnlyLocalStorage(false)
                .numOfColumns(3)
                .theme(R.style.Shergil)
                .allowPreview(true)
                .maxSelectable(3)
                .allowCamera(true)
                .showDeviceCamera(false)
                .showCameraFirst(false)
                .orientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .withRequestCode(REQUEST_SHERGIL)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SHERGIL) {
            findViewById<TextView>(R.id.hello).text =
                Shergil.getMediaUris(data).toString().replace(", ", "\n")
        }
    }
}
