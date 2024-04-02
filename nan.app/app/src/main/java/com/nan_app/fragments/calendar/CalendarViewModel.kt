package com.nan_app.fragments.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Calendar
import com.nan_app.entities.Clients
import org.koin.java.KoinJavaComponent

class CalendarViewModel : ViewModel() {

    private val clientSource: FirebaseDataClientSource by KoinJavaComponent.inject(
        FirebaseDataClientSource::class.java
    )

    companion object {
        const val STATE_INIT = "init"
        const val STATE_LOAD_LIST = "load_list"
    }

    var viewState: MutableLiveData<String> = MutableLiveData()
    var calendarList: MutableLiveData<List<String>> = MutableLiveData()

    fun loadState(state: String) {
        when (state) {
            STATE_INIT -> {
                viewState.value = STATE_INIT
            }

            STATE_LOAD_LIST -> {
                viewState.value = STATE_LOAD_LIST
            }

        }
    }

    fun loadListCalendar() {
        calendarList.value = Calendar().timeList
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
}