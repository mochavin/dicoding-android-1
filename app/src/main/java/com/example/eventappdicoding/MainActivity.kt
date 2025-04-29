package com.example.eventappdicoding

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eventappdicoding.databinding.ActivityMainBinding // ViewBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Get the NavHostFragment using the FragmentManager
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as androidx.navigation.fragment.NavHostFragment
        // Retrieve the NavController from the NavHostFragment
        val navController = navHostFragment.navController


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                // Masukkan ID top-level fragment (yang ada di bottom nav)
                R.id.navigation_active_events, R.id.navigation_finished_events
            )
        )
        // Setup ActionBar (jika ada) dengan NavController
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Setup BottomNavigationView dengan NavController
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Ensure navController is initialized before using it (good practice)
        if (!::navController.isInitialized) {
            return super.onSupportNavigateUp()
        }
        // Delegate navigation to the NavController, using the appBarConfiguration.
        // Fall back to the default super implementation if NavController doesn't handle it.
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}