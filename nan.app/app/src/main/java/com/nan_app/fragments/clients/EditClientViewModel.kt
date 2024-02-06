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

    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java
    )

    fun getClient(): Clients{
        viewState.value = STATE_LOADING
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

                viewState.value= STATE_DONE
            }
            else
                viewState.value= STATE_ERROR
        }



    }
    fun init(){
        viewState.value = STATE_INIT
    }
}