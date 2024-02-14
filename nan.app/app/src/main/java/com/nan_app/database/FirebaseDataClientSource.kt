package com.nan_app.database

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nan_app.entities.Clients
import kotlinx.coroutines.tasks.await


class FirebaseDataClientSource: ClientSource {

    val db = Firebase.firestore
    val storage = Firebase.storage

    private val collectionName: String = "clients"
    private val collection = db.collection(collectionName)


    var clientFb: Clients = Clients()
    var currentClient: Clients = Clients()
    var clientListFB = mutableListOf<Clients>()


    override suspend fun loadClientById(id: Int): Boolean {
        val querySnapshot = collection
            .whereEqualTo("id", id)
            .get()
            .await()
        return !querySnapshot.isEmpty
    }

    override suspend fun loadClientByName() {
    }

    override suspend fun loadClientByLastName() {
    }

    override suspend fun loadAllClients() {
        val clientList = mutableListOf<Clients>()
        val querySnapshot = collection
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            for (document in querySnapshot.documents) {
                val client = document.toObject<Clients>()
                if (client != null) {
                    clientList.add(client)
                } else {
                    throw IllegalStateException("No document found")
                }
                clientListFB = clientList
            }
        } else {
            clientListFB = clientList
        }
    }

    override suspend fun deleteClient(id: Int) {
        try {
            val querySnapshot = collection
                .whereEqualTo("id", id)
                .get()
                .await()
            val documentRef = querySnapshot
                .first()
                .reference
                .delete()
                .await()

        } catch (e: Exception) {
            throw IllegalStateException("Failed to remove product into Firestore: ${e.message}")
        }
    }

    override suspend fun insertClient(newClient: Clients) {
        try {
            collection.add(newClient).await()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to insert product into Firestore: ${e.message}")
        }
    }

    override suspend fun updateClientById(
        id: Int,
        field: String,
        value: String,
        reference: String
    ) {

        collection
            .document(reference)
            .update(
                mapOf(
                    field to value
                ),
            )
            .await()
    }

    override suspend fun getClientReference(id: Int):String {
        try {
            val querySnapshot = collection
                .whereEqualTo("id", id)
                .get()
                .await()
                .first()
                .reference
            return querySnapshot.id
        } catch (e: Exception) {
            Log.e("TAG", "Error al obtener la referencia del cliente: ${e.message}")
            throw e
        }
    }

    override suspend fun loadImageUri(uri: Uri): String {
        val storageRef = storage.reference
        val fileRef = storageRef.child("images/${uri.lastPathSegment}")
        fileRef.putFile(uri).await() // Esperar a que se complete la carga
        val downloadUrl = fileRef.downloadUrl.await() // Esperar a que se obtenga la URL de descarga
        return downloadUrl.toString()
    }
}