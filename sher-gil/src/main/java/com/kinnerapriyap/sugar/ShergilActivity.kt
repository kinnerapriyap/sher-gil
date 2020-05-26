package com.kinnerapriyap.sugar

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyRecyclerView
import com.kinnerapriyap.sugar.resultlauncher.GetMultipleFromGallery
import com.kinnerapriyap.sugar.resultlauncher.GetFromGalleryInput
import com.kinnerapriyap.sugar.resultlauncher.ResultLauncherHandler
import java.util.ArrayList

internal class ShergilActivity : AppCompatActivity() {

    private val epoxyRecyclerView: EpoxyRecyclerView
        get() = findViewById(R.id.epoxy_recycler_view)

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private val observer: ResultLauncherHandler by lazy {
        ResultLauncherHandler(
            this,
            ::setGalleryResult,
            ::setPermissionResult
        )
    }

    private val getFromGalleryInput by lazy {
        GetFromGalleryInput(
            mimeTypes = choiceSpec.mimeTypes,
            allowOnlyLocalStorage = choiceSpec.allowOnlyLocalStorage,
            allowMultipleSelection = choiceSpec.allowMultipleSelection
        )
    }

    private val controller: ShergilController by lazy {
        ShergilController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)

        // TODO: Make variable
        controller.spanCount = 3
        epoxyRecyclerView.setControllerAndBuildModels(controller)
    }

    override fun onResume() {
        super.onResume()
        //TODO: Dont do this here
        blah()
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

    private fun setGalleryResult(imageUriList: List<Uri>) {
        val resultIntent =
            Intent().apply {
                putParcelableArrayListExtra(
                    GetMultipleFromGallery.RESULT_URIS,
                    imageUriList as? ArrayList
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
}
