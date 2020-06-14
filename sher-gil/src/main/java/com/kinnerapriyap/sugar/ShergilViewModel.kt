package com.kinnerapriyap.sugar

import android.app.Application
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.*
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellUpdateModel
import com.kinnerapriyap.sugar.mediagallery.media.MediaGalleryCursorWrapper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ShergilViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private val mediaGalleryHandler by lazy {
        MediaGalleryHandler(application.contentResolver)
    }

    private val selectedMediaCellDisplayModels by lazy {
        MutableLiveData<MutableList<MediaCellDisplayModel>>().apply { value = mutableListOf() }
    }

    private var updatedMediaCellPositions: Pair<Int, Int> = Pair(-1, -1)

    private var cursor: LiveData<Cursor?> = liveData {
        emit(
            mediaGalleryHandler.fetchMedia(
                mimeTypes = choiceSpec.mimeTypes,
                showDisallowedMimeTypes = choiceSpec.showDisallowedMimeTypes,
                allowCamera = choiceSpec.allowCamera
            )
        )
    }

    private val errorMessage by lazy {
        MutableLiveData<String?>().apply { value = null }
    }

    /**
     * Providing [Dispatchers.Main] in coroutineContext as default
     * to use [launch], which is an extension function of [CoroutineScope],
     * without specifying the thread each time
     * [Dispatchers.IO] or [Dispatchers.Default] may be used
     * when required to change the thread
     */
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun getChoiceSpec() = choiceSpec

    fun allowMultipleSelection() = choiceSpec.maxSelectable > 1

    fun getMediaCellUpdateModel(): LiveData<MediaCellUpdateModel> =
        Transformations.map(selectedMediaCellDisplayModels) { models ->
            MediaCellUpdateModel(updatedMediaCellPositions, models)
        }

    fun getSelectedMedia(): LiveData<MutableList<MediaCellDisplayModel>> =
        selectedMediaCellDisplayModels

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

    fun fetchAlbumCursor(): Cursor? = mediaGalleryHandler.fetchAlbum(cursor.value)

    fun clear() {
        cursor.value?.close()
    }
}
