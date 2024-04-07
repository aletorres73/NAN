package com.nan_app.fragments.clients.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class HomeViewModel : ViewModel() {

    var listClients: MutableLiveData<MutableList<Clients>> = MutableLiveData()
    var viewState: MutableLiveData<String> = MutableLiveData()

    private val clientSource: FirebaseDataClientSource by inject(FirebaseDataClientSource::class.java)

    fun init() {
        viewState.value = STATE_INIT
    }

    fun getList() {
        viewModelScope.launch {
            clientSource.clientFb.let { clientSource.loadAllClients() }
            if (clientSource.clientListFB.isEmpty()) {
                viewState.value = STATE_EMPTY
            } else {
                listClients.value = clientSource.clientListFB
                setClientStateValue(clientSource.clientListFB)
            }
        }
    }

    fun refresh() {
        viewState.value = STATE_INIT
    }

    fun getCurrentClient(position: Int) {
        clientSource.setCurrentClient(position)
    }

    private suspend fun setClientStateValue(list: List<Clients>) {
        viewModelScope.launch {
            var state: String

            list.forEachIndexed { index, client ->
                val finishDay = client.FinishDay
                if (finishDay != "") {

                    val formatter1 = DateTimeFormatter.ofPattern("d/M/yyyy")
                    val formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                    val finishDate: LocalDate = try {
                        LocalDate.parse(finishDay, formatter1)
                    } catch (e: DateTimeParseException) {
                        LocalDate.parse(finishDay, formatter2)
                    }

                    val currentDate = LocalDate.now()
                    val daysUntilFinish = currentDate.until(finishDate).days
                    state = when {
                        daysUntilFinish < 0 -> {
                            "Vencido"
                        }

                        daysUntilFinish in 0..5 -> {
                            "Por Vencer"
                        }

                        else -> {
                            "Al día"
                        }
                    }

                    val flag = when (client.State) {
                        state -> true
                        else -> {
                            false
                        }
                    }
                    if (!flag) {
                        listClients.value!![index].State = state
                        clientSource.updateClientById(
                            client.id,
                            "state",
                            state,
                        )
                    }
                }
            }
            viewState.value = STATE_LOADING
        }
    }

    fun searchByName(query: String) {
        val filteredByName = listClients.value!!.filter {
            it.Name
                .lowercase()
                .contains(query)
        }
        if (filteredByName.isNotEmpty()) {
            listClients.value = filteredByName.toMutableList()
            viewState.value = STATE_LOADING
        } else
            listClients.value = clientSource.clientListFB
        viewState.value = STATE_LOADING
    }

    companion object {
        const val STATE_ERROR = "error"
        const val STATE_LOADING = "loading"
        const val STATE_EMPTY = "empty"
        const val STATE_INIT = "init"
        const val STATE_DELETE = "delete"
        const val STATE_WAIT = "wait"
    }


}