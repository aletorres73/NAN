package com.nan_app.database

import com.nan_app.entities.Clients

interface ClientSource {

    suspend fun loadClientById(id: Int): Boolean
    suspend fun loadClientByName()
    suspend fun loadClientByLastName()
    suspend fun loadAllClients()
    suspend fun deleteClient(id : Int)
    suspend fun insertClient(newClient: Clients)
//    suspend fun getLastIdfromList():Int
}