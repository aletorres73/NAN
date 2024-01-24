package com.nan_app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nan_app.entities.clientModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class StartActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalContext.startKoin {
            androidContext(this@StartActivity)
            modules(clientModule)
        }
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        val contextActivity = this
        val intent = Intent(contextActivity, MainActivity::class.java)
        startActivity(intent)
    }
}