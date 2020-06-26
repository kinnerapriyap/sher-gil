package com.kinnerapriyap.sugar

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellUpdateModel
import com.kinnerapriyap.sugar.mediagallery.media.MediaGalleryCursorWrapper

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

    private val askPermissionAndOpenCameraCapture = MutableLiveData<SingleLiveEvent<Boolean>>()

    private val askPermissionAndOpenMediaGallery = MutableLiveData<SingleLiveEvent<Boolean>>()

    private var updatedMediaCellPositions: Pair<Int, Int> = Pair(-1, -1)

    private var cursor: MutableLiveData<Cursor?> = MutableLiveData<Cursor?>()

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

    fun setCameraCaptureUri(uri: Uri) {
        cameraCaptureUri = uri
    }

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
        if (selected.any { it.id == displayModel.id }) {
            selected.removeAll { it.id == displayModel.id }
        } else {
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
        updatedMediaCellPositions = Pair(displayModel.position, updatedMediaCellPositions.first)
        selectedMediaCellDisplayModels.value = selected
    }

    private fun isSelectedOverMax(): Boolean =
        selectedMediaCellDisplayModels.value?.size?.let {
            it >= choiceSpec.maxSelectable && it != 1
        } ?: false

    fun getCursor(): LiveData<Cursor?> = cursor

    fun getCurrentMediaCursor(bucketDisplayName: String? = null): Cursor? =
        MediaGalleryCursorWrapper(cursor.value, bucketDisplayName)

    fun fetchAlbumCursor(): Cursor? =
        mediaGalleryHandler.fetchAlbum(cursor.value, choiceSpec.allowCamera)

    fun closeCursor() {
        cursor.value?.close()
    }

    fun getSelectedAlbumSpinnerName(): LiveData<String?> =
        selectedAlbumSpinnerName

    fun setSelectedAlbumSpinnerName(bucketDisplayName: String?) {
        selectedAlbumSpinnerName.value = bucketDisplayName
    }

    fun getAskPermissionAndOpenCameraCapture(): LiveData<SingleLiveEvent<Boolean>> =
        askPermissionAndOpenCameraCapture

    fun setAskPermissionAndOpenCameraCapture() {
        askPermissionAndOpenCameraCapture.value = SingleLiveEvent(true)
    }

    fun getAskPermissionAndOpenMediaGallery(): LiveData<SingleLiveEvent<Boolean>> =
        askPermissionAndOpenMediaGallery

    fun setAskPermissionAndOpenMediaGallery() {
        askPermissionAndOpenMediaGallery.value = SingleLiveEvent(true)
    }
}
