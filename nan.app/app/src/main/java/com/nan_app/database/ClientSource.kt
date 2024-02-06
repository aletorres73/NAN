package com.nan_app.database

import com.nan_app.entities.Clients
import java.lang.ref.PhantomReference

interface ClientSource {

    suspend fun loadClientById(id: Int): Boolean
    suspend fun loadClientByName()
    suspend fun loadClientByLastName()
    suspend fun loadAllClients()
    suspend fun deleteClient(id : Int)
    suspend fun insertClient(newClient: Clients)
    suspend fun updateClientById(id: Int, field : String, value: String, reference: String)
    suspend fun getClientReference(id: Int): String
}