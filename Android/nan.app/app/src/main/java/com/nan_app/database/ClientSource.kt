package com.nan_app.database

interface ClientSource {

    suspend fun loadClientById()
    suspend fun loadClientByName()
    suspend fun loadClientByLastName()
    suspend fun loadAllClients()
}