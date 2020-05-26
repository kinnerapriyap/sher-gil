package com.kinnerapriyap.sugar

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.databinding.MediaCellListener
import com.kinnerapriyap.sugar.resultlauncher.GetFromGalleryInput
import com.kinnerapriyap.sugar.resultlauncher.GetMultipleFromGallery
import com.kinnerapriyap.sugar.resultlauncher.ResultLauncherHandler
import java.util.ArrayList

internal class ShergilActivity : AppCompatActivity(), MediaCellListener {

    private val epoxyRecyclerView: EpoxyRecyclerView
        get() = findViewById(R.id.epoxy_recycler_view)

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private lateinit var observer: ResultLauncherHandler

    private val getFromGalleryInput by lazy {
        GetFromGalleryInput(
            mimeTypes = choiceSpec.mimeTypes,
            allowOnlyLocalStorage = choiceSpec.allowOnlyLocalStorage,
            allowMultipleSelection = choiceSpec.allowMultipleSelection
        )
    }

    private val controller: ShergilController by lazy {
        ShergilController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)

        observer = ResultLauncherHandler(this, ::setGalleryResult, ::setPermissionResult)
        blah()

        val spanCount = choiceSpec.numOfColumns
        val layoutManager = GridLayoutManager(this, spanCount)
        controller.spanCount = spanCount
        layoutManager.spanSizeLookup = controller.spanSizeLookup
        epoxyRecyclerView.layoutManager = layoutManager
        epoxyRecyclerView.setControllerAndBuildModels(controller)
    }

    fun blah() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ->
                observer.askPermission()
            else ->
                observer.openGallery(getFromGalleryInput)
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun setGalleryResult(mediaUriList: List<Uri>) {
        val resultIntent =
            Intent().apply {
                putParcelableArrayListExtra(
                    GetMultipleFromGallery.RESULT_URIS,
                    mediaUriList as? ArrayList
                )
            }
        //setResult(Activity.RESULT_OK, resultIntent)
        //finish()
        val map = mediaUriList.map { it to true }.toMap()
        mediaList = map
        controller.setMediaList(map)
    }

    var mediaList: Map<Uri, Boolean> = emptyMap()

    private fun setPermissionResult(allowed: Boolean) {
        if (allowed) {
            observer.openGallery(getFromGalleryInput)
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onMediaCellClicked(view: View, uri: Uri) {
        val map = mediaList.map { (euri, isChecked) ->
            euri to if (euri == uri) !isChecked else isChecked
        }.toMap()
        mediaList = map
        controller.setMediaList(map)
    }
}
