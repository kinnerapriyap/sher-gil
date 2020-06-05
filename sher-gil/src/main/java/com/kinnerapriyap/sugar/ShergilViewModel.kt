package com.kinnerapriyap.sugar

import android.app.Application
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.kinnerapriyap.sugar.choice.ChoiceSpec
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellUpdateModel
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
import com.kinnerapriyap.sugar.mediagallery.media.MediaGalleryCursorWrapper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ShergilViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val choiceSpec: ChoiceSpec = ChoiceSpec.instance

    private val mediaGalleryHandler by lazy {
        MediaGalleryHandler(getApplication<Application>().contentResolver)
    }

    private var mediaCellDisplayModels: MutableList<MediaCellDisplayModel> = mutableListOf()

    private val updatedMediaCellPosition = MutableLiveData<MediaCellUpdateModel>()

    private var cursor: LiveData<Cursor?> = liveData {
        emit(mediaGalleryHandler.fetchMedia(choiceSpec.mimeTypes))
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

    fun getSelectedMediaUriList(): List<Uri> =
        mediaCellDisplayModels.map { it.mediaUri }

    fun getUpdatedMediaCellPosition(): LiveData<MediaCellUpdateModel> = updatedMediaCellPosition

    fun setMediaChecked(displayModel: MediaCellDisplayModel) {
        updatedMediaCellPosition.value =
            MediaCellUpdateModel(displayModel.position, !displayModel.isChecked)
        mediaCellDisplayModels =
            if (mediaCellDisplayModels.contains(displayModel)) {
                mediaCellDisplayModels.map {
                    val isChecked =
                        if (it == displayModel) {
                            !it.isChecked
                        } else it.isChecked
                    it.copy(isChecked = isChecked)
                }
            } else {
                val new = mediaCellDisplayModels
                new.add(displayModel.copy(isChecked = !displayModel.isChecked))
                new
            }.toMutableList()
    }

    fun getCursor(): LiveData<Cursor?> = cursor

    fun getCurrentMediaCursor(bucketDisplayName: String? = null): Cursor? =
        MediaGalleryCursorWrapper(cursor.value, bucketDisplayName)

    fun fetchAlbumCursor(): Cursor? = mediaGalleryHandler.fetchAlbum(cursor.value)

    fun clear() {
        cursor.value?.close()
    }
}