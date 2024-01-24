package com.nan_app.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class HomeViewModel : ViewModel() {

    var ClientListDb: MutableLiveData<MutableList<Clients>> = MutableLiveData()
    var viewState: MutableLiveData<String> = MutableLiveData()

    private val clientSource: FirebaseDataClientSource by inject(FirebaseDataClientSource::class.java)


    fun init(){
        viewState.value = STATE_INIT
    }

    fun getList(){
        viewModelScope.launch {
            clientSource.clientFb.let { clientSource.loadAllClients() }
            if(clientSource.clientlistFB.isNullOrEmpty()){
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


    companion object {
        const val STATE_ERROR   = "error"
        const val STATE_DONE    = "done"
        const val STATE_LOADING = "loading"
        const val STATE_EMPTY   = "empty"
        const val STATE_INIT    = "init"
    }


}