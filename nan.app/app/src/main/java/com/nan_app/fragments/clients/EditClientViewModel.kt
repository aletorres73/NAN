package com.nan_app.fragments.clients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class EditClientViewModel : ViewModel() {

     var viewState : MutableLiveData<String> = MutableLiveData()

/*    companion object {
        const val STATE_ERROR   = "error"
        const val STATE_DONE    = "done"
        const val STATE_LOADING = "loading"
        const val STATE_INIT    = "init"
    }*/

    companion object {

        const val STATE_DONE_UPDATE_CLIENT  = "STATE_DONE_NEW_CLIENT"
        const val STATE_ERROR_UPDATE_CLIENT = "STATE_DONE_UPDATE_CLIENT"
        const val STATE_LOAD_NEW_IMAGE      = "STATE_LOAD_NEW_IMAGE"
        const val STATE_GALLERY             = "STATE_GALLERY"
        const val STATE_CAMERA              = "STATE_CAMERA"
        const val STATE_DELETE_IMAGE        = "STATE_DELETE_IMAGE"
        const val STATE_IMAGE_EMPTY         = "STATE_IMAGE_EMPTY"
        const val STATE_DONE_IMAGE_DELETE   = "STATE_DONE_IMAGE_DELETE"
        const val STATE_ERROR_IMAGE_DELETE  = "STATE_ERROR_IMAGE_DELETE"
        const val STATE_INIT                = "STATE_INIT"
    }
    fun loadState(state : String){
         when(state){
            "init"              ->{viewState.value = STATE_INIT}
            "doneUpdateClient"  ->{viewState.value = STATE_DONE_UPDATE_CLIENT}
            "errorUpdateClient" ->{viewState.value = STATE_ERROR_UPDATE_CLIENT}
            "loadNewImage"      ->{viewState.value = STATE_LOAD_NEW_IMAGE}
            "openGallery"       ->{viewState.value = STATE_GALLERY}
            "openCamera"        ->{viewState.value = STATE_CAMERA}
            "deleteImage"       ->{viewState.value = STATE_DELETE_IMAGE}
            "emptyImage"        ->{viewState.value = STATE_IMAGE_EMPTY}
            "imageDeleted"      ->{viewState.value = STATE_DONE_IMAGE_DELETE}
            "errorImageDelete"  ->{viewState.value = STATE_ERROR_IMAGE_DELETE}
         }
    }
    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java
    )
    fun getClient(): Clients{
        return clientSource.currentClient
    }
    fun updatedClient(editedClient : Clients, id : Int){
        viewModelScope.launch {
            val referenceClient = clientSource.getClientReference(id)
            if(clientSource.loadClientById(id)){
                if(editedClient.Name != "")
                    clientSource.updateClientById(id, "name", editedClient.Name, referenceClient)
                if(editedClient.LastName != "")
                    clientSource.updateClientById(id, "lastName", editedClient.LastName, referenceClient)
                if(editedClient.Birthday != "")
                    clientSource.updateClientById(id, "birthday", editedClient.Birthday, referenceClient)
                if(editedClient.Phone != "")
                    clientSource.updateClientById(id, "phone", editedClient.Phone, referenceClient)
                if(editedClient.Email != "")
                    clientSource.updateClientById(id, "email", editedClient.Email, referenceClient)
                if(editedClient.PayDay != "")
                    clientSource.updateClientById(id, "payDay", editedClient.PayDay, referenceClient)
                if(editedClient.FinishDay != "")
                    clientSource.updateClientById(id, "finishDay", editedClient.FinishDay, referenceClient)
                if(editedClient.AmountClass != "")
                    clientSource.updateClientById(id, "amountClass", editedClient.AmountClass, referenceClient)

                loadState("doneUpdateClient")
            }
            else
                loadState("errorUpdateClient")
        }
    }
    fun updateClientByImage(client: Clients, id: Int){
        viewModelScope.launch {
            val referenceClient = clientSource.getClientReference(id)
            if(clientSource.loadClientById(id)){
                clientSource.updateClientById(id, "imageName",client.ImageName,referenceClient)
                clientSource.updateClientById(id, "imageUri",client.ImageUri,referenceClient)
            }
        }
    }
    fun deleteImage(imageName : String){
        viewModelScope.launch {
            if(clientSource.deleteImage(imageName))
                loadState("imageDeleted")
            else
                loadState("errorImageDelete")
        }
    }
}