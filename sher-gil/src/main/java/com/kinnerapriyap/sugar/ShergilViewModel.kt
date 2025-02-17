package com.kinnerapriyap.sugar

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.kinnerapriyap.sugar.camera.CameraState
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler.Companion.ALL_ALBUM_BUCKET_DISPLAY_NAME
import com.kinnerapriyap.sugar.mediagallery.album.MediaGalleryAlbum
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellUpdateModel
import com.kinnerapriyap.sugar.mediagallery.media.DEFAULT_ID
import com.kinnerapriyap.sugar.mediagallery.media.MediaGalleryCursorWrapper
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ShergilViewModel(application: Application) : AndroidViewModel(application) {

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private val mediaGalleryHandler by lazy {
        MediaGalleryHandler(application.contentResolver)
    }

    private val selectedMediaCellDisplayModels by lazy {
        MutableLiveData<MutableList<MediaCellDisplayModel>>().apply { value = mutableListOf() }
    }

    private val selectedAlbumSpinnerName by lazy {
        MutableLiveData<String?>().apply { value = null }
    }

    private val cameraState by lazy {
        MutableLiveData<CameraState>().apply { value = CameraState.CAPTURE }
    }

    private var updatedMediaCellPositions: Pair<Int, Int> = Pair(-1, -1)

    private var cursor: MutableLiveData<Cursor?> = MutableLiveData()

    fun fetchCursor() {
        cursor.postValue(
            mediaGalleryHandler.fetchMedia(
                mimeTypes = choiceSpec.mimeTypes,
                showDisallowedMimeTypes = choiceSpec.showDisallowedMimeTypes,
                allowCamera = choiceSpec.allowCamera
            )
        )
    }

    private var cameraCaptureUri: Uri? = null

    fun getCameraCaptureUri() = cameraCaptureUri

    fun resetCameraCaptureUri() {
        cameraCaptureUri =
            getApplication<Application>().contentResolver?.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )
    }

    private val errorMessage by lazy {
        MutableLiveData<String?>().apply { value = null }
    }

    fun getChoiceSpec() = choiceSpec

    fun allowMultipleSelection() = choiceSpec.maxSelectable > 1

    fun getMediaCellUpdateModel(): LiveData<MediaCellUpdateModel> =
        Transformations.map(selectedMediaCellDisplayModels) { models ->
            MediaCellUpdateModel(updatedMediaCellPositions, models)
        }

    fun getSelectedMediaCellDisplayModels(): MutableList<MediaCellDisplayModel> =
        selectedMediaCellDisplayModels.value ?: mutableListOf()

    fun getSelectedMediaCount(): LiveData<Int> =
        Transformations.map(selectedMediaCellDisplayModels) { it.size }

    fun getSelectedMediaUriList(): List<Uri> =
        selectedMediaCellDisplayModels.value?.map { it.mediaUri } ?: emptyList()

    fun getErrorMessage(): LiveData<String?> = errorMessage

    fun setMediaChecked(displayModel: MediaCellDisplayModel) {
        val selected = selectedMediaCellDisplayModels.value ?: mutableListOf()
        when {
            displayModel.id == DEFAULT_ID -> {
                errorMessage.value =
                    getApplication<Application>().resources.getString(R.string.invalid_id)
                return
            }
            selected.any { it.id == displayModel.id } -> {
                selected.removeAll { it.id == displayModel.id }
            }
            else -> {
                if (isSelectedOverMax()) {
                    errorMessage.value =
                        getApplication<Application>().resources.getString(
                            R.string.max_selectable_error,
                            choiceSpec.maxSelectable
                        )
                    return
                }
                if (!allowMultipleSelection()) selected.removeAll(selected)
                selected.add(displayModel.copy(isChecked = true))
            }
        }
        updatedMediaCellPositions = Pair(displayModel.position, updatedMediaCellPositions.first)
        selectedMediaCellDisplayModels.value = selected
    }

    private fun isSelectedOverMax(): Boolean =
        selectedMediaCellDisplayModels.value?.size?.let {
            it >= choiceSpec.maxSelectable && it != 1
        } ?: false

    fun getCursor(): LiveData<Cursor?> = cursor

    fun getCurrentMediaCursor(bucketDisplayName: String? = null): Cursor =
        MediaGalleryCursorWrapper(cursor.value, bucketDisplayName)

    fun fetchAlbums(): List<MediaGalleryAlbum> {
        val addedNamesCount: MutableMap<String, Int> = mutableMapOf()
        val cursor = cursor.value ?: return emptyList()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val bucketColumnIndex = cursor.getColumnIndex(MediaGalleryHandler.BUCKET_DISPLAY_NAME)
            if (bucketColumnIndex == -1) break
            val name = cursor.getStringOrNull(bucketColumnIndex)
            cursor.moveToNext()
            if (name == null || name == ALL_ALBUM_BUCKET_DISPLAY_NAME) continue
            else if (!addedNamesCount.contains(name)) {
                addedNamesCount[name] = 1
            } else {
                addedNamesCount[name] = (addedNamesCount[name] ?: 0) + 1
            }
        }
        var allCount = cursor.count
        if (choiceSpec.allowCamera) allCount -= 1
        cursor.moveToFirst()
        return mutableListOf(MediaGalleryAlbum(ALL_ALBUM_BUCKET_DISPLAY_NAME, allCount)).apply {
            addAll(addedNamesCount.map { MediaGalleryAlbum(it.key, it.value) })
        }
    }

    fun getSelectedAlbumSpinnerName(): LiveData<String?> =
        selectedAlbumSpinnerName

    fun setSelectedAlbumSpinnerName(bucketDisplayName: String?) {
        selectedAlbumSpinnerName.value = bucketDisplayName
    }

    fun getCameraState(): LiveData<CameraState> = cameraState

    fun setCameraState(state: CameraState) {
        cameraState.value = state
    }

    fun insertCameraImage(
        fileName: String,
        mimeType: String,
        bitmap: Bitmap
    ) {
        val contentResolver = getApplication<Application>().contentResolver ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val pictureContentUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return
        contentResolver.openFileDescriptor(pictureContentUri, "w").use { pfd ->
            pfd?.let {
                try {
                    val fos = FileOutputStream(it.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        contentValues.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        }
        contentResolver.update(pictureContentUri, contentValues, null, null)
    }
}
