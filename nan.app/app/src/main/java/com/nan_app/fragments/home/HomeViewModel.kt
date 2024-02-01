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

    var ClientListDb    : MutableLiveData<MutableList<Clients>> = MutableLiveData()
    var viewState       : MutableLiveData<String> = MutableLiveData()
    var viewAction      : MutableLiveData<String>  = MutableLiveData()
    private val clientSource: FirebaseDataClientSource by inject(FirebaseDataClientSource::class.java)


    fun init(){
        viewState.value = STATE_INIT
        viewAction.value = STATE_INIT
    }

    fun getList(){
        viewModelScope.launch {
            clientSource.clientFb.let { clientSource.loadAllClients() }
            if(clientSource.clientlistFB.isEmpty()){
                viewState.value = STATE_EMPTY
            }else{
                viewState.value = STATE_LOADING
            }
        }
    }
    fun loadList() {
        ClientListDb.value = clientSource.clientlistFB
        viewState.value = STATE_DONE
    }
    fun refresh(){
        viewState.value = STATE_INIT
    }

    fun deleteClient( position: Int){
        val id = clientSource.clientlistFB[position].id
        viewModelScope.launch {
            if(clientSource.loadClientById(id)){
                clientSource.deleteClient(id)
                viewState.value = STATE_DELETE
            }
            else viewState.value = STATE_ERROR
        }
    }

    fun createClient(){

    }

    fun clickOnItem(){
        viewAction.value = STATE_LAST
    }


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