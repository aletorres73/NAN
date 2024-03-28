package com.nan_app.fragments.clients


import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class CreateClientViewModel : ViewModel() {

    var viewState: MutableLiveData<String> = MutableLiveData()
    var viewUrl: MutableLiveData<String> = MutableLiveData()
    var viewImageName: MutableLiveData<String> = MutableLiveData()
    var viewImageuri: MutableLiveData<Uri> = MutableLiveData()

    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java
    )

    companion object {
        const val STATE_LOAD_NEW_CLIENT = "state_load_new_client"
        const val STATE_ERROR_NEW_CLIENT = "state_error_new_client"
        const val STATE_DONE_NEW_CLIENT = "state_done_new_client"
        const val STATE_LOAD_NEW_IMAGE = "state_load_new_image"
        const val STATE_GALLERY = "state_gallery"
        const val STATE_CAMERA = "state_camera"
        const val STATE_DELETE_IMAGE = "state_delete_image"
        const val STATE_IMAGE_EMPTY = "state_image_empty"
        const val STATE_DONE_IMAGE_DELETE = "state_done_image_delete"
        const val STATE_ERROR_IMAGE_DELETE = "state_error_image_delete"
        const val STATE_INIT = "state_init"
        const val STATE_SELECT_BIRTHDAY = "state_select_birthday"
        const val STATE_SELECT_PAYDAY = "state_select_payday"
        const val STATE_SELECT_FINISHDAY = "state_select_finish_day"
        const val STATE_WAIT = "state_wait"
    }

    fun loadState(state: String) {
        when (state) {
            STATE_INIT -> {
                viewState.value = STATE_INIT
            }

            STATE_LOAD_NEW_CLIENT -> {
                viewState.value = STATE_LOAD_NEW_CLIENT
            }

            STATE_ERROR_NEW_CLIENT -> {
                viewState.value = STATE_ERROR_NEW_CLIENT
            }

            STATE_DONE_NEW_CLIENT -> {
                viewState.value = STATE_DONE_NEW_CLIENT
            }

            STATE_LOAD_NEW_IMAGE -> {
                viewState.value = STATE_LOAD_NEW_IMAGE
            }

            STATE_GALLERY -> {
                viewState.value = STATE_GALLERY
            }

            STATE_CAMERA -> {
                viewState.value = STATE_CAMERA
            }

            STATE_DELETE_IMAGE -> {
                viewState.value = STATE_DELETE_IMAGE
            }

            STATE_IMAGE_EMPTY -> {
                viewState.value = STATE_IMAGE_EMPTY
            }

            STATE_DONE_IMAGE_DELETE -> {
                viewState.value = STATE_DONE_IMAGE_DELETE
            }

            STATE_ERROR_IMAGE_DELETE -> {
                viewState.value = STATE_ERROR_IMAGE_DELETE
            }

            STATE_SELECT_BIRTHDAY -> {
                viewState.value = STATE_SELECT_BIRTHDAY
            }

            STATE_SELECT_PAYDAY -> {
                viewState.value = STATE_SELECT_PAYDAY
            }

            STATE_SELECT_FINISHDAY -> {
                viewState.value = STATE_SELECT_FINISHDAY
            }

            STATE_WAIT -> {
                viewState.value = STATE_WAIT
            }

        }

    }

    fun loadNewClient(newClient: Clients) {
        viewModelScope.launch {
            if (clientSource.insertClient(newClient))
                loadState(STATE_DONE_NEW_CLIENT)
            else
                loadState(STATE_ERROR_NEW_CLIENT)
        }
    }
    fun getImageName(): String {
        return if (viewImageName.value != null)
            viewImageName.value!!
        else
            ""
    }

    fun saveImage(image: Uri) {
        clientSource.deleteImageName = image.lastPathSegment.toString()
        viewImageuri.value = image
        viewImageName.value = image.lastPathSegment.toString()

    }

    fun checkID(id: Int): Boolean {
        val iterator = clientSource.clientListFB.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().id == id)
                return true
        }
        return false
    }

    fun loadImage(newClient: Clients) {

        viewModelScope.launch {
            viewUrl.value = viewImageuri.value?.let { clientSource.loadImageUri(it) }
            newClient.ImageUri = viewUrl.value.toString()
            newClient.ImageName = viewImageName.value.toString()

            viewImageuri.value = "".toUri()
            viewImageName.value = ""
            viewUrl.value = ""

            loadState(STATE_LOAD_NEW_CLIENT)

        }
    }
}