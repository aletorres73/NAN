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

    override suspend fun loadClientById(id : Int): Boolean {
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
                clientlistFB = clientList
            }
        } else {
            clientlistFB = clientList
        }
    }

    override suspend fun deleteClient(id : Int) {
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
//            clientlistFB.add(newClients)
            collection.add(newClient).await()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to insert product into Firestore: ${e.message}")
        }
    }

/*    override suspend fun getLastIdfromList(): Int {

    }*/


}