package com.nan_app.database

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nan_app.entities.Clients
import kotlinx.coroutines.tasks.await


class FirebaseDataClientSource: ClientSource {

    val db= Firebase.firestore
    private val collectionName: String ="clients"
    private val collection= db.collection(collectionName)

    var clientFb: Clients = Clients()
    var clientlistFB = mutableListOf<Clients>()
    var currentClient: Boolean = false

    lateinit var currentClientId: String

    override suspend fun loadClientById() {
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
                    throw IllegalStateException("No document found with the specified ID")
                }
                clientlistFB = clientList
            }
        } else {
            clientlistFB = clientList
        }
    }


}