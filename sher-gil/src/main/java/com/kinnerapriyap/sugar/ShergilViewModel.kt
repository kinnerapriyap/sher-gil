package com.kinnerapriyap.sugar

import android.app.Application
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.*
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
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
        MutableLiveData<List<MediaCellDisplayModel>>()
    }

    private var updatedMediaCellPosition: Int = -1

    private var cursor: LiveData<Cursor?> = liveData {
        emit(
            mediaGalleryHandler.fetchMedia(
                mimeTypes = choiceSpec.mimeTypes,
                showDisallowedMimeTypes = choiceSpec.showDisallowedMimeTypes
            )
        )
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

    fun getMediaCellUpdateModel(): LiveData<MediaCellUpdateModel> =
        Transformations.map(selectedMediaCellDisplayModels) { models ->
            MediaCellUpdateModel(updatedMediaCellPosition, models)
        }

    fun getSelectedMediaUriList(): List<Uri> =
        selectedMediaCellDisplayModels.value?.map { it.mediaUri } ?: emptyList()

    fun setMediaChecked(displayModel: MediaCellDisplayModel) {
        updatedMediaCellPosition = displayModel.position
        val new = selectedMediaCellDisplayModels.value?.toMutableList() ?: mutableListOf()
        if (new.any { it.id == displayModel.id }) {
            new.removeAll { it.id == displayModel.id }
        } else {
            new.add(displayModel)
        }
        selectedMediaCellDisplayModels.value = new
    }

    fun getCursor(): LiveData<Cursor?> = cursor

    fun getCurrentMediaCursor(bucketDisplayName: String? = null): Cursor? =
        MediaGalleryCursorWrapper(cursor.value, bucketDisplayName)

    fun fetchAlbumCursor(): Cursor? = mediaGalleryHandler.fetchAlbum(cursor.value)

    fun clear() {
        cursor.value?.close()
    }
}