package com.kinnerapriyap.sugar

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kinnerapriyap.sugar.mediagallery.MediaCellDisplayModel
import com.kinnerapriyap.sugar.extension.toMediaCellDisplayModel

class ShergilViewModel : ViewModel() {
    private val mediaCellDisplayModels =
        MutableLiveData<List<MediaCellDisplayModel>>().apply { value = emptyList() }

    fun getMediaCellDisplayModels(): LiveData<List<MediaCellDisplayModel>> =
        mediaCellDisplayModels

    fun initialiseMediaCellDisplayModels(mediaUriList: List<Uri>) {
        mediaCellDisplayModels.value =
            mediaUriList.map { it.toMediaCellDisplayModel(false) }

    }

    fun setCheckedMedia(uri: Uri) {
        mediaCellDisplayModels.value =
            mediaCellDisplayModels.value?.map {
                val isChecked =
                    if (it.mediaUri == uri) !it.isChecked
                    else it.isChecked
                it.copy(isChecked = isChecked)
            }
    }
}