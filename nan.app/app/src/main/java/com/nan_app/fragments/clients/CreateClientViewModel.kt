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

    var viewState     : MutableLiveData<String> = MutableLiveData()
    var viewUrl       : MutableLiveData<String> = MutableLiveData()
    var viewImageName : MutableLiveData<String> = MutableLiveData()
    var viewImageuri  : MutableLiveData<Uri>    = MutableLiveData()

    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java)

    companion object {
        const val STATE_LOAD_NEW_CLIENT     = "STATE_LOAD_NEW_CLIENT"
        const val STATE_ERROR_NEW_CLIENT    = "STATE_ERROR_NEW_CLIENT"
        const val STATE_DONE_NEW_CLIENT     = "STATE_DONE_NEW_CLIENT"
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
            "newClient"         ->{viewState.value = STATE_LOAD_NEW_CLIENT}
            "errorClientLoad"   ->{viewState.value = STATE_ERROR_NEW_CLIENT}
            "newClientLoad"     ->{viewState.value = STATE_DONE_NEW_CLIENT}
            "loadNewImage"      ->{viewState.value = STATE_LOAD_NEW_IMAGE}
            "openGallery"       ->{viewState.value = STATE_GALLERY}
            "openCamera"        ->{viewState.value = STATE_CAMERA}
            "deleteImage"       ->{viewState.value = STATE_DELETE_IMAGE}
            "emptyImage"        ->{viewState.value = STATE_IMAGE_EMPTY}
            "imageDeleted"      ->{viewState.value = STATE_DONE_IMAGE_DELETE}
            "errorImageDelete"  ->{viewState.value = STATE_ERROR_IMAGE_DELETE}
        }

    }

    fun loadNewClient(newClient : Clients){
        viewModelScope.launch {


            viewUrl.value = viewImageuri.value?.let { clientSource.loadImageUri(it) }
            newClient.ImageUri = viewUrl.value.toString()
            newClient.ImageName = viewImageName.value.toString()


            if(clientSource.insertClient(newClient))
                loadState("newClientLoad")
            else
                loadState("errorClientLoad")

            viewImageuri.value  = "".toUri()
            viewImageName.value = ""
            viewUrl.value       = ""
        }
    }
    fun uploadImage(data : Uri) {
        clientSource.deleteImageName = data.lastPathSegment.toString()
        viewImageName.value          = data.lastPathSegment.toString()

        viewModelScope.launch {
            viewUrl.value                = clientSource.loadImageUri(data)
        }
    }
    fun getImageName(): String{
        return if(viewImageName.value != null)
            viewImageName.value!!
        else
            ""
    }

    fun deleteImage(imageName : String){
        viewModelScope.launch {
            if(clientSource.deleteImage(imageName))
                loadState("imageDeleted")
            else
                loadState("errorImageDelete")
        }
    }

    fun saveImage(image : Uri){
        clientSource.deleteImageName = image.lastPathSegment.toString()
        viewImageuri.value  = image
        viewImageName.value = image.lastPathSegment.toString()

    }
}