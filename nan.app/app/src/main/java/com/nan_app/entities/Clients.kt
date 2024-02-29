package com.nan_app.entities

import android.provider.ContactsContract.CommonDataKinds.Phone

class Clients() {

    var id          : Int     =0
    var Name        : String  =""
    var LastName    : String  =""
    var Birthday    : String  =""
    var PayDay      : String  =""
    var FinishDay   : String  =""
    var State       : String  =""
    var AmountClass : String  =""
    var ImageUri    : String  =""
    var Email       : String  =""
    var Phone       : String  =""
    var ImageName   : String  =""
    constructor (
        id         : Int,
        Name       : String,
        LastName   : String,
        Birthday   : String,
        PayDay     : String,
        FinisDay   : String,
        State      : String,
        AmountClass: String,
        ImageUri   : String,
        Email      : String,
        Phone      : String,
        ImageName  : String

    ):this() {
        this.id          = id
        this.Name        = Name
        this.LastName    = LastName
        this.Birthday    = Birthday
        this.PayDay      = PayDay
        this.FinishDay   = FinisDay
        this.State       = State
        this.AmountClass = AmountClass
        this.ImageUri    = ImageUri
        this.Email       = Email
        this.Phone       = Phone
        this.ImageName   = ImageName
    }
}