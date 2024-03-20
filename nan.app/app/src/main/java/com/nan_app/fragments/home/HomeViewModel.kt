package com.nan_app.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.text.FieldPosition

class HomeViewModel : ViewModel() {

    //    var ClientListDb    : MutableLiveData<MutableList<Clients>> = MutableLiveData()
    var viewState       : MutableLiveData<String> = MutableLiveData()

    private val clientSource: FirebaseDataClientSource by inject(FirebaseDataClientSource::class.java)

    fun init(){
        viewState.value = STATE_INIT
    }

    fun getList(){
        viewModelScope.launch {
            clientSource.clientFb.let { clientSource.loadAllClients() }
            if(clientSource.clientListFB.isEmpty()){
                viewState.value = STATE_EMPTY
            }else{
                viewState.value = STATE_LOADING
            }
        }
    }
    fun loadList() : MutableList<Clients> {
        return if(clientSource.clientListFB.isEmpty())
            clientSource.clientListFB.toMutableList()
        else
            clientSource.clientListFB.sortedBy{it.id}.toMutableList()
    }
    fun refresh(){
        viewState.value = STATE_INIT
    }
    fun doneState(){
        viewState.value = STATE_DONE
    }

    fun deleteClient( position: Int){
        val client = clientSource.clientListFB
        viewModelScope.launch {
            if(clientSource.loadClientById(client[position].id)){
                if(client[position].ImageName != "" || client[position].ImageName != "null")
                    clientSource.deleteImage(client[position].ImageName)
                clientSource.deleteClient(client[position].id)
                /*                client.removeAt(position)
                                ClientListDb.value = client*/
                viewState.value = STATE_DELETE
            }
            else viewState.value = STATE_ERROR
        }

    }
    fun getCurrentClient(position: Int){
        clientSource.currentClient = clientSource.clientListFB[position]
    }

//    fun removeItemList(deletePosition: Int) {
//        val remove = clientSource.clientListFB
//        remove.removeAt(deletePosition)
//        ClientListDb.value = remove
//    }

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


}