package com.nan_app.entities

class Clients() {

    var id:         Int=0
    var name:       String=""
    var lastname:   String=""

    constructor (
        id:         Int,
        name:       String,
        lastname:   String,

    ):this(){
        this.id           = id
        this.name         = name
        this.lastname     = lastname
    }
}