package com.nan_app.fragments.clients

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class EditClientViewModel : ViewModel() {

    var viewState: MutableLiveData<String> = MutableLiveData()
    var currentClient: MutableLiveData<Clients> = MutableLiveData()

    private var viewUrl: MutableLiveData<String> = MutableLiveData()
    private var viewImageName: MutableLiveData<String> = MutableLiveData()
    private var viewImageUrl: MutableLiveData<Uri> = MutableLiveData()


    companion object {

        const val STATE_DONE_UPDATE_CLIENT = "state_done_update_client"
        const val STATE_ERROR_UPDATE_CLIENT = "state_error_update_client"
        const val STATE_LOAD_NEW_IMAGE = "state_load_new_image"
        const val STATE_GALLERY = "state_galery"
        const val STATE_CAMERA = "state_camera"
        const val STATE_BUTTON_DELETE_IMAGE = "state_boton_delete_image"
        const val STATE_DELETE_IMAGE = "state_delete_image"
        const val STATE_IMAGE_EMPTY = "state_image_empty"
        const val STATE_DONE_IMAGE_DELETE = "state_done_image_delete"
        const val STATE_ERROR_IMAGE_DELETE = "state_error_image_delete"
        const val STATE_INIT = "state_init"
        const val STATE_SELECT_BIRTHDAY = "state_select_birthday"
        const val STATE_SELECT_PAYDAY = "state_select_payday"
        const val STATE_SELECT_FINISHDAY = "state_select_finishday"
        const val STATE_ERROR_DELETE_CLIENT = "state_error_delete_client"
        const val STATE_CLIENT_DELETED = "state_client_deleted"
    }

    fun loadState(state: String) {
        when (state) {
            STATE_INIT -> {
                viewState.value = STATE_INIT
            }

            STATE_DONE_UPDATE_CLIENT -> {
                viewState.value = STATE_DONE_UPDATE_CLIENT
            }

            STATE_ERROR_UPDATE_CLIENT -> {
                viewState.value = STATE_ERROR_UPDATE_CLIENT
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

            STATE_BUTTON_DELETE_IMAGE -> {
                viewState.value = STATE_BUTTON_DELETE_IMAGE
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

            STATE_CLIENT_DELETED -> {
                viewState.value = STATE_CLIENT_DELETED
            }

            STATE_ERROR_DELETE_CLIENT -> {
                viewState.value = STATE_ERROR_DELETE_CLIENT
            }

        }
    }

    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java
    )

    fun getClient() {
        currentClient.value = clientSource.currentClient
    }

    fun updatedClient(editedClient: Clients, id: Int) {
        viewModelScope.launch {
            val referenceClient = clientSource.getClientReference(id)
            if (clientSource.loadClientById(id)) {
                if (editedClient.Name != "") clientSource.updateClientById(
                    id, "name", editedClient.Name, referenceClient
                )
                if (editedClient.LastName != "") clientSource.updateClientById(
                    id, "lastName", editedClient.LastName, referenceClient
                )
                if (editedClient.Birthday != "") clientSource.updateClientById(
                    id, "birthday", editedClient.Birthday, referenceClient
                )
                if (editedClient.Phone != "") clientSource.updateClientById(
                    id, "phone", editedClient.Phone, referenceClient
                )
                if (editedClient.Email != "") clientSource.updateClientById(
                    id, "email", editedClient.Email, referenceClient
                )
                if (editedClient.PayDay != "") clientSource.updateClientById(
                    id, "payDay", editedClient.PayDay, referenceClient
                )
                if (editedClient.FinishDay != "") clientSource.updateClientById(
                    id, "finishDay", editedClient.FinishDay, referenceClient
                )
                if (editedClient.AmountClass != "") clientSource.updateClientById(
                    id, "amountClass", editedClient.AmountClass, referenceClient
                )
                if (editedClient.ImageUri != "") if (viewImageUrl.value != null) {
                    viewUrl.value = clientSource.loadImageUri(viewImageUrl.value!!)
                    clientSource.updateClientById(
                        id, "imageUri", viewUrl.value!!, referenceClient
                    )
                }
                if (editedClient.ImageName != "") if (editedClient.ImageUri != "null") clientSource.updateClientById(
                    id, "imageName", editedClient.ImageName, referenceClient
                )
                loadState(STATE_DONE_UPDATE_CLIENT)
            } else loadState(STATE_ERROR_UPDATE_CLIENT)
        }
    }

    fun updateClientByImage(client: Clients, id: Int) {
        viewModelScope.launch {
            val referenceClient = clientSource.getClientReference(id)
            if (clientSource.loadClientById(id)) {
                clientSource.updateClientById(id, "imageName", client.ImageName, referenceClient)
                clientSource.updateClientById(id, "imageUri", client.ImageUri, referenceClient)
            }
        }
    }

    fun deleteImage(imageName: String) {
        viewModelScope.launch {
            if (clientSource.deleteImage(imageName)) loadState(STATE_DONE_IMAGE_DELETE)
            else loadState(STATE_ERROR_IMAGE_DELETE)
        }
    }

    fun saveImage(image: Uri) {
        clientSource.deleteImageName = image.lastPathSegment.toString()
        viewImageUrl.value = image
        viewImageName.value = image.lastPathSegment.toString()

    }

    fun getUri(): String {
        return viewImageUrl.value.toString()
    }

    fun getImageName(): String {
        return viewImageName.value.toString()
    }

    fun deleteClient(id: Int, imageName: String) {
        viewModelScope.launch {
            if (clientSource.deleteClient(id)) {
                if (imageName != "")
                    if (clientSource.deleteImage(imageName))
                        loadState(STATE_CLIENT_DELETED)
                    else
                        loadState(STATE_ERROR_IMAGE_DELETE)
                loadState(STATE_CLIENT_DELETED)
            } else
                loadState(STATE_ERROR_DELETE_CLIENT)
        }
    }
}