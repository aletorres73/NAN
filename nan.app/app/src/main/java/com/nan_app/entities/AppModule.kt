package com.nan_app.entities


import com.nan_app.database.ClientSource
import com.nan_app.database.FirebaseDataClientSource
import org.koin.dsl.bind
import org.koin.dsl.module

val clientModule = module {
//    single<UserSource> {FirebaseDataUserSource()}
    single { FirebaseDataClientSource() } bind ClientSource::class

}