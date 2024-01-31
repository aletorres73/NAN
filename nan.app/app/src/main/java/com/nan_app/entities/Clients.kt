package com.nan_app.entities

class Clients() {

    var id:         Int=0
    var Name:       String=""
    var LastName:   String=""

    constructor (
        id:         Int,
        Name:       String,
        LastName:   String,

    ):this(){
        this.id           = id
        this.Name         = Name
        this.LastName     = LastName
    }
}