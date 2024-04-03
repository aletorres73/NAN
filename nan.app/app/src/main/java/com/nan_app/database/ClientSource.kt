package com.nan_app.database

import android.net.Uri
import com.nan_app.entities.Clients

interface ClientSource {

    suspend fun loadClientById(id: Int): Boolean
    suspend fun loadAllClients()
    suspend fun deleteClient(id: Int): Boolean
    suspend fun insertClient(newClient: Clients): Boolean
    suspend fun updateClientById(id: Int, field: String, value: Any, reference: String)
    suspend fun getClientReference(id: Int): String
    suspend fun loadImageUri(uri: Uri): String
    suspend fun deleteImage(path: String): Boolean
}