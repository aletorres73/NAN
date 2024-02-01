package com.nan_app.database

interface ClientSource {

    suspend fun loadClientById(id: Int): Boolean
    suspend fun loadClientByName()
    suspend fun loadClientByLastName()
    suspend fun loadAllClients()
    suspend fun deleteClient(id : Int)
    suspend fun createClient()
}