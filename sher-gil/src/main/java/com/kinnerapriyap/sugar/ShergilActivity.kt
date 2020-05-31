package com.kinnerapriyap.sugar

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryAlbumCursorAdapter
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryAlbumCursorWrapper
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryFragment
import com.kinnerapriyap.sugar.resultlauncher.GetFromGalleryInput
import com.kinnerapriyap.sugar.resultlauncher.GetMultipleFromGallery
import com.kinnerapriyap.sugar.resultlauncher.ResultLauncherHandler
import kotlinx.android.synthetic.main.activity_shergil.*
import java.util.ArrayList

internal class ShergilActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private lateinit var observer: ResultLauncherHandler

    private val viewModel: ShergilViewModel by viewModels()

    private val mediaGalleryAlbumCursorAdapter by lazy {
        MediaGalleryAlbumCursorAdapter(
            this,
            MediaGalleryAlbumCursorWrapper(viewModel.fetchAlbumCursor())
        )
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
    }

    private val getFromGalleryInput by lazy {
        GetFromGalleryInput(
            mimeTypes = choiceSpec.mimeTypes,
            allowOnlyLocalStorage = choiceSpec.allowOnlyLocalStorage,
            allowMultipleSelection = choiceSpec.allowMultipleSelection
        )
    }

    companion object {
        private const val MEDIA_GALLERY_FRAGMENT_TAG = "mediaGalleryFragmentTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Shergil)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        albumSpinner.adapter = mediaGalleryAlbumCursorAdapter
        albumSpinner.onItemSelectedListener = this

        viewModel.getMediaCellDisplayModels().observe(this, Observer {
            Log.e("kin output count", it.filter { m -> m.isChecked }.size.toString())
        })

        observer = ResultLauncherHandler(this, ::setGalleryResult, ::setPermissionResult)
        blah()
    }

    fun blah() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ->
                observer.askPermission()
            else -> {
                //observer.openGallery(getFromGalleryInput)
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.container,
                        MediaGalleryFragment.newInstance(),
                        MEDIA_GALLERY_FRAGMENT_TAG
                    )
                    .commit()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun setGalleryResult(mediaUriList: List<Uri>) {
        // viewModel.initialiseMediaCellDisplayModels(mediaUriList)
    }

    private fun setShergilResult(mediaUriList: List<Uri>) {
        val resultIntent =
            Intent().apply {
                putParcelableArrayListExtra(
                    GetMultipleFromGallery.RESULT_URIS,
                    mediaUriList as? ArrayList
                )
            }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun setPermissionResult(allowed: Boolean) {
        if (allowed) {
            observer.openGallery(getFromGalleryInput)
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        setSelectedSpinnerName(null)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cursor = mediaGalleryAlbumCursorAdapter.getItem(position) as? Cursor
        val bucketDisplayName =
            mediaGalleryAlbumCursorAdapter.convertToString(cursor).toString()
        setSelectedSpinnerName(bucketDisplayName)
    }

    private fun setSelectedSpinnerName(bucketDisplayName: String?) {
        val mediaGalleryFragment: MediaGalleryFragment? =
            supportFragmentManager.findFragmentByTag(MEDIA_GALLERY_FRAGMENT_TAG) as? MediaGalleryFragment
        mediaGalleryFragment?.setSelectedSpinnerName(bucketDisplayName)
    }
}
