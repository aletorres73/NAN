package com.nan_app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import com.nan_app.R
import com.nan_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navHost: NavHost
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHost
        navController = navHost.navController
        supportActionBar?.hide()


        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeButton -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.createClient -> {
                    navController.navigate(R.id.createClientFragment)
                    true
                }
                R.id.calendarClient->{
                    navController.navigate(R.id.calendarFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentDestinationId = navController.currentDestination?.id
        if (currentDestinationId == R.id.homeFragment) {
            if (navController.previousBackStackEntry == null) {
                super.onBackPressed()
                finish()
                finishAffinity()
            } else {
                finishAffinity()
            }
        } else {
            Toast.makeText(this, "Presione una vez más para salir", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.homeFragment)
        }
    }

}