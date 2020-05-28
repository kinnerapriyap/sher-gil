package com.kinnerapriyap.sugar

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kinnerapriyap.sugar.extension.toMediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ShergilViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val mediaGalleryHandler by lazy {
        MediaGalleryHandler(getApplication<Application>().contentResolver)
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

    private val mediaCellDisplayModels =
        MutableLiveData<List<MediaCellDisplayModel>>().apply { value = emptyList() }

    fun getMediaCellDisplayModels(): LiveData<List<MediaCellDisplayModel>> =
        mediaCellDisplayModels

    fun setCheckedMedia(uri: Uri) {
        mediaCellDisplayModels.value =
            mediaCellDisplayModels.value?.map {
                val isChecked =
                    if (it.mediaUri == uri) !it.isChecked
                    else it.isChecked
                it.copy(isChecked = isChecked)
            }
    }

    fun setMediaCellDisplayModels() {
        launch {
            mediaCellDisplayModels.value = withContext(Dispatchers.IO) {
               mediaGalleryHandler.fetchMedia()
            }
        }
    }
}