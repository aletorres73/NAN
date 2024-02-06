package com.nan_app.fragments.clients


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class CreateClientViewModel : ViewModel() {

    var viewState : MutableLiveData<String> = MutableLiveData()

    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java)

    companion object {
        const val STATE_ERROR   = "error"
        const val STATE_DONE    = "done"
        const val STATE_LOADING = "loading"
        const val STATE_EMPTY   = "empty"
        const val STATE_INIT    = "init"
        const val STATE_REMOVING= "removing"
        const val STATE_LAST    = "last"
        const val STATE_DELETE  = "delete"
    }

    fun initState(){
        viewState.value = STATE_INIT
    }
    fun error(){
        viewState.value = STATE_ERROR
    }
    fun loading(){
        viewState.value = STATE_LOADING
    }
    fun done(){
        viewState.value = STATE_DONE
    }

    fun makeNewClient(newClient : Clients){
        viewModelScope.launch {
            clientSource.insertClient(newClient)
        }
    }

}