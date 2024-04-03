package com.nan_app.fragments.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class CalendarViewModel : ViewModel() {

    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java
    )

    companion object {
        const val STATE_INIT = "init"
        const val STATE_LOAD_LIST = "load_list"
        const val STATE_WAIT = "wait"
    }

    var viewState: MutableLiveData<String> = MutableLiveData()

    fun loadState(state: String) {
        when (state) {
            STATE_INIT -> {
                viewState.value = STATE_INIT
            }

            STATE_LOAD_LIST -> {
                viewState.value = STATE_LOAD_LIST
            }

            STATE_WAIT -> {
                viewState.value = STATE_WAIT
            }
        }
    }

    fun getLisClient(): List<Clients> {
        return clientSource.clientListFB

    }

    fun getListNameClient(): List<String> {
        val listName = emptyList<String>().toMutableList()
        for (client in clientSource.clientListFB) {
            listName.add("${client.Name} ${client.LastName}")
        }
        return listName.toList()
    }

    fun getClientId(position: Int): Int {
        return clientSource.clientListFB[position].id
    }

    fun setClientOnCalendar(clientId: Int, time: String, day: String): Boolean {

        val client = clientSource.clientListFB.filter { it.id == clientId }
        if (client.isNotEmpty()) {
            val dateClient = client[0].dates
            val amountClass = client[0].AmountClass

            if(checkSizeDateClient(dateClient, amountClass)){
                dateClient[day] = time
                viewModelScope.launch {
                    val referenceClient = clientSource.getClientReference(clientId)
                    clientSource.updateClientById(clientId, "dates", dateClient, referenceClient)
                    loadState(STATE_LOAD_LIST)
                }
            }
            else
                return false
        }
        return true
    }

    private fun checkSizeDateClient(dateClient: HashMap<String, String>, amountClass: String): Boolean {
        var index = 0
        val numberValid = amountClass.toInt()/4
        for (date in dateClient){
            if(date.value.isNotEmpty())
                index ++
        }
        return index != numberValid

    }
}